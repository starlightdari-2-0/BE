package com.example.startlight.pet.service;

import com.example.startlight.pet.dto.FlaskResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FlaskService {
    private final RestTemplate restTemplate;

    @Value("${ml.api}")
    public String apiUrl;
    /**
     * 공통 POST 요청 로직
     */
    public <T> ResponseEntity<T> sendPostRequest(String url, Map<String, ?> requestBody, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, ?>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            return restTemplate.postForEntity(url, requestEntity, responseType);
        } catch (Exception e) {
            throw new RuntimeException("Flask 서버 요청 실패: " + e.getMessage());
        }
    }

    /**
     * 추가 Flask API 호출
     */
    public FlaskResponseDto processImgFlaskApi(String imageUrl, Integer selectedX, Integer selectedY) {
        String additionalApiUrl = apiUrl + "/stars_process_image";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("image_url", imageUrl);
        requestBody.put("point", new Integer[]{selectedX, selectedY});

        ResponseEntity<String> response = sendPostRequest(additionalApiUrl, requestBody, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                // ✅ JSON 응답을 DTO로 매핑
                ObjectMapper objectMapper = new ObjectMapper();
                FlaskResponseDto flaskResponse = objectMapper.readValue(response.getBody(), FlaskResponseDto.class);
                return flaskResponse;
            } catch (Exception e) {
                throw new RuntimeException("Flask 응답 매핑 실패: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("추가 Flask 서버 응답 실패: " + response.getStatusCode());
        }
    }

    FlaskResponseDto testMLApi() {
        FlaskResponseDto flaskResponseDto = new FlaskResponseDto();
        flaskResponseDto.setSvgPath("https://starlightbucket.s3.amazonaws.com/test_user/kkong9.svg");
        // ✅ edges 설정
        List<List<Integer>> edges = Arrays.asList(
                Arrays.asList(0, 1), Arrays.asList(1, 14), Arrays.asList(3, 10), Arrays.asList(4, 12),
                Arrays.asList(6, 5), Arrays.asList(6, 7), Arrays.asList(6, 14), Arrays.asList(7, 2),
                Arrays.asList(8, 2), Arrays.asList(9, 8), Arrays.asList(9, 10), Arrays.asList(11, 12),
                Arrays.asList(13, 3), Arrays.asList(13, 12)
        );
        flaskResponseDto.setEdges(edges);

        // ✅ majorPoints 설정
        List<List<Integer>> majorPoints = Arrays.asList(
                Arrays.asList(459, 200), Arrays.asList(470, 255), Arrays.asList(256, 346),
                Arrays.asList(134, 273), Arrays.asList(129, 121), Arrays.asList(310, 296),
                Arrays.asList(314, 335), Arrays.asList(287, 374), Arrays.asList(253, 298),
                Arrays.asList(258, 253), Arrays.asList(175, 271), Arrays.asList(157, 217),
                Arrays.asList(110, 213), Arrays.asList(116, 252), Arrays.asList(367, 339)
        );
        flaskResponseDto.setMajorPoints(majorPoints);
        return flaskResponseDto;
    }
}
