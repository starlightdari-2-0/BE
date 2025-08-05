package com.example.startlight.chatMessage.controller;

import com.example.startlight.chatMessage.dto.ChatAnswerDto;
import com.example.startlight.chatMessage.dto.ChatMessageRepDto;
import com.example.startlight.chatMessage.dto.ChatMessageReqDto;
import com.example.startlight.chatMessage.service.ChatMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @PostMapping
    ResponseEntity<?> createChatMessage(@RequestBody ChatMessageReqDto chatMessageReqDto) throws JsonProcessingException {
        ChatAnswerDto chatAnswer = chatMessageService.createChatAnswer(chatMessageReqDto);
        return ResponseEntity.ok().body(chatAnswer);
    }

    @GetMapping("/all")
    ResponseEntity<List<ChatMessageRepDto>> getAllChatMessages() {
        List<ChatMessageRepDto> chatMessages = chatMessageService.getChatMessages();
        return ResponseEntity.ok().body(chatMessages);
    }
}
