package com.example.startlight.chatMessage.service;

import com.example.startlight.chatMessage.dao.ChatMessageDao;
import com.example.startlight.chatMessage.dto.ChatAnswerDto;
import com.example.startlight.chatMessage.dto.ChatMessageRepDto;
import com.example.startlight.chatMessage.dto.ChatMessageReqDto;
import com.example.startlight.chatMessage.entity.ChatMessage;
import com.example.startlight.kakao.util.UserUtil;
import com.example.startlight.member.dao.MemberDao;
import com.example.startlight.member.entity.Member;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageDao chatMessageDao;
    private final MemberDao memberDao;

    private final RestTemplate restTemplate;

    @Value("${ml.api}")
    private String mlUrl;
    /**
     * 공통 POST 요청 로직
     */
    private <T> ResponseEntity<T> sendPostRequest(String url, Map<String, ?> requestBody, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, ?>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            return restTemplate.postForEntity(url, requestEntity, responseType);
        } catch (Exception e) {
            throw new RuntimeException("Flask 서버 요청 실패: " + e.getMessage());
        }
    }

    @Transactional
    public ChatAnswerDto createChatAnswer(ChatMessageReqDto chatMessageReqDto) throws JsonProcessingException {
        String apiUrl = mlUrl + "/rag_get_answer";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("route_num", chatMessageReqDto.getCategory());
        requestBody.put("query", chatMessageReqDto.getQuestion());

        Long userId = UserUtil.getCurrentUserId();
        Member member = memberDao.selectMember(userId);
        ResponseEntity<String> response = sendPostRequest(apiUrl, requestBody, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            ChatMessage chatMessage = ChatMessage.builder()
                    .category(chatMessageReqDto.getCategory())
                    .question(chatMessageReqDto.getQuestion())
                    .member(member).build();
            ChatMessage savedMessage = chatMessageDao.save(chatMessage);
            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            // "answer" 필드 추출
            String answer = jsonNode.get("answer").asText();
            ChatAnswerDto answerDto = ChatAnswerDto.builder()
                    .chatId(savedMessage.getChat_id())
                    .answer(answer).build();
            savedMessage.saveAnswer(answer);
            return answerDto;
        }
        else {
            throw new RuntimeException("추가 Flask 서버 응답 실패: " + response.getStatusCode());
        }
    }

    public List<ChatMessageRepDto> getChatMessages() {
        Long userId = UserUtil.getCurrentUserId();
        List<ChatMessage> messageByUserId = chatMessageDao.findMessageByUserId(userId);
        return messageByUserId.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 매핑 로직 작성
    private ChatMessageRepDto convertToDto(ChatMessage entity) {
        return ChatMessageRepDto.builder()
                .chatId(entity.getChat_id())
                .category(entity.getCategory())
                .question(entity.getQuestion())
                .createdAt(entity.getCreatedAt())
                .answer(entity.getAnswer())
                .memberId(entity.getMember().getMember_id())
                .build();
    }
}
