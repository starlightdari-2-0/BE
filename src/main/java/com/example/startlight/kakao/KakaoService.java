package com.example.startlight.kakao;

import com.example.startlight.kakao.dto.KakaoTokenResponseDto;
import com.example.startlight.kakao.dto.KakaoUserInfoResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class KakaoService {

    private final String clientId;
    private final String redirectUri;
    private final String awsUrl;
    private final String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
    private final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";

    @Autowired
    public KakaoService(
            @Value("${kakao.client.id}") String clientId,
            @Value("${kakao.redirect.uri}") String uri,
            @Value("${aws.api}") String awsUrl
    ) {
        this.clientId = clientId;
        this.redirectUri = uri;
        this.awsUrl = awsUrl;
    }

    public String getAccessTokenFromKakao(String code) {

        log.info("Request Params: grant_type=authorization_code, client_id={}, redirect_uri={}, code={}", clientId, redirectUri, code);

        // 동기적으로 WebClient 요청 수행
        String response = WebClient.create(KAUTH_TOKEN_URL_HOST)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", clientId)
                        .with("redirect_uri", redirectUri)
                        .with("code", code))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> {
                    // 상태 코드와 응답 Body를 로그로 출력
                    return clientResponse.bodyToMono(String.class).flatMap(body -> {
                        log.error("Error Response: Status={}, Body={}", clientResponse.statusCode(), body);
                        return Mono.error(new RuntimeException("Invalid Parameter: " + body));
                    });
                })
                .bodyToMono(String.class)
                .block(); // 동기적으로 응답 대기

        log.info("Kakao API Response: {}", response);

        // JSON -> DTO 변환 및 accessToken 추출
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 추가 필드 무시
            KakaoTokenResponseDto dto = objectMapper.readValue(response, KakaoTokenResponseDto.class);
            log.info("Access Token: {}", dto.getAccessToken());
            return dto.getAccessToken(); // accessToken 반환
        } catch (JsonProcessingException e) {
            log.error("JSON Parsing Error: {}", e.getMessage());
            throw new RuntimeException("Failed to parse response: " + response, e);
        }
    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {

        KakaoUserInfoResponseDto userInfo = WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                //TODO : Custom Exception
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();

        log.info("[ Kakao Service ] Auth ID ---> {} ", userInfo.getId());
        log.info("[ Kakao Service ] NickName ---> {} ", userInfo.getKakaoAccount().getProfile().getNickName());
        log.info("[ Kakao Service ] ProfileImageUrl ---> {} ", userInfo.getKakaoAccount().getProfile().getProfileImageUrl());

        return userInfo;
    }

    public boolean logoutFromKakao(String accessToken) {
        log.info("Logging out user with accessToken: {}", accessToken);

        String response = WebClient.create(KAUTH_USER_URL_HOST)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v1/user/logout")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(body -> {
                            log.error("Logout API Error: Status={}, Body={}", clientResponse.statusCode(), body);
                            return Mono.error(new RuntimeException("Failed to logout: " + body));
                        })
                )
                .bodyToMono(String.class)
                .block(); // 동기 실행

        log.info("Kakao Logout Response: {}", response);
        return response != null;  // 성공 여부를 반환
    }

    public String getMyPageUrl() {
        return awsUrl + "/mypage";
    }

    public String getOnboardingUrl() {
        return awsUrl + "/onboarding";
    }
}
