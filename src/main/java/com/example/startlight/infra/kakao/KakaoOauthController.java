package com.example.startlight.infra.kakao;

import com.example.startlight.global.config.JWTUtils;
import com.example.startlight.infra.kakao.dto.KakaoUserCreateDto;
import com.example.startlight.infra.kakao.dto.KakaoUserInfoResponseDto;
import com.example.startlight.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/kakao")
public class KakaoOauthController{
    private final KakaoService kakaoService;
    private final JWTUtils jwtTokenProvider;
    private final MemberService memberService;

    @GetMapping("/callback")
    public ResponseEntity<?> kakaoLogin(HttpServletRequest request, HttpServletResponse response) {
        log.debug("kakao login");
        String code = request.getParameter("code");
        log.debug("Received code: {}", code); // 디버그 출력 추가
        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body("Authorization code is missing");
        }

        try {
            // 1. Access Token 가져오기
            String accessToken = kakaoService.getAccessTokenFromKakao(code);

            // 2. 사용자 정보 가져오기
            KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

            // 3. JWT 생성
            boolean rememberMe = "true".equals(request.getParameter("rememberMe"));
            JWTUtils.TokenDto tokens = jwtTokenProvider.createTokens(userInfo, accessToken, rememberMe);

            // 4. 인증 객체 확인
            Authentication authentication = jwtTokenProvider.verifyAndGetAuthentication(tokens.getAccessToken());
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            boolean isSecure = request.getScheme().equals("https");
            String sameSiteValue = isSecure ? "None" : "Lax";

            // 5. Access Token을 쿠키에 저장
            final ResponseCookie accessTokenCookie = ResponseCookie.from("AUTH-TOKEN", tokens.getAccessToken())
                    .httpOnly(true)
                    .maxAge(60 * 60 * 24) // TODO: 1시간으로 줄이기
                    .path("/")
                    .secure(isSecure)
                    .sameSite(sameSiteValue)
                    .build();

            System.out.println("AUTH-TOKEN : "+tokens.getAccessToken());

            // 6. Refresh Token을 쿠키에 저장
            final ResponseCookie refreshTokenCookie = ResponseCookie.from("REFRESH-TOKEN", tokens.getRefreshToken())
                    .httpOnly(true)
                    .maxAge(rememberMe ? 30 * 24 * 60 * 60 : 14 * 24 * 60 * 60) // 30일 or 14일
                    .path("/")
                    .secure(isSecure)
                    .sameSite(sameSiteValue)
                    .build();

            System.out.println("REFRESH-TOKEN : "+tokens.getRefreshToken());

            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
            response.setHeader("Access-Control-Expose-Headers", "Set-Cookie");

            KakaoUserCreateDto kakaoUserCreateDto = KakaoUserCreateDto.builder()
                    .id(userInfo.getId())
                    .nickName(userInfo.getKakaoAccount().profile.getNickName())
                    .email(userInfo.getKakaoAccount().email)
                    .profileImageUrl(userInfo.getKakaoAccount().profile.getProfileImageUrl())
                    .build();

            boolean isFirstLogin = !memberService.isFirstKakaoLogin(userInfo.getId());
            memberService.loginMember(kakaoUserCreateDto);

            String redirectUri = isFirstLogin
                    ? kakaoService.getOnboardingUrl()
                    : kakaoService.getMyPageUrl();

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(redirectUri))
                    .build();

        } catch (Exception e) {
            log.error("Error during Kakao login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Login failed: " + e.getMessage());
        }

    }

    /**
     *  Refresh Token으로 Access Token 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 1. 쿠키에서 Refresh Token 추출
            Cookie[] cookies = request.getCookies();
            String refreshToken = null;
            String oldAccessToken = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("REFRESH-TOKEN".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                    } else if ("AUTH-TOKEN".equals(cookie.getName())) {
                        oldAccessToken = cookie.getValue();
                    }
                }
            }

            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Refresh token not found"));
            }

            // 2. Refresh Token 유효성 검증
            if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired refresh token"));
            }

            // 3. 카카오 액세스 토큰 추출 (기존 Access Token에서)
            String kakaoAccessToken = null;
            if (oldAccessToken != null) {
                try {
                    kakaoAccessToken = jwtTokenProvider.extractKakaoAccessToken(oldAccessToken);
                } catch (Exception e) {
                    log.warn("Failed to extract Kakao access token from old JWT", e);
                }
            }

            // 카카오 액세스 토큰이 없으면 에러
            if (kakaoAccessToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Kakao access token not found"));
            }

            // 4. 새로운 Access Token 발급
            String newAccessToken = jwtTokenProvider.refreshAccessToken(refreshToken, kakaoAccessToken);
            log.info("New Access Token issued via refresh");

            boolean isSecure = request.getScheme().equals("https");
            String sameSiteValue = isSecure ? "None" : "Lax";

            // 5. 새 Access Token을 쿠키에 설정
            final ResponseCookie accessTokenCookie = ResponseCookie.from("AUTH-TOKEN", newAccessToken)
                    .httpOnly(true)
                    .maxAge(60 * 60) // 1시간
                    .path("/")
                    .secure(isSecure)
                    .sameSite(sameSiteValue)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

            return ResponseEntity.ok(Map.of(
                    "message", "Token refreshed successfully",
                    "accessToken", newAccessToken
            ));

        } catch (RuntimeException e) {
            log.error("Error during token refresh", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token refresh failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> kakaoLogout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        boolean isSecure = request.getScheme().equals("https");
        String sameSiteValue = isSecure ? "None" : "Lax";
        try {
            // 1. 쿠키에서 AUTH-TOKEN 추출
            Cookie[] cookies = request.getCookies();
            String jwtToken = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("AUTH-TOKEN".equals(cookie.getName())) {
                        jwtToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (jwtToken == null || jwtToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No valid session found.");
            }

            log.info("Received JWT Token from Cookie: {}", jwtToken);

            // 2. JWT 검증 및 사용자 ID 추출
            Authentication authentication = jwtTokenProvider.verifyAndGetAuthentication(jwtToken);
            Long userId = null;
            if (authentication != null && authentication.getPrincipal() instanceof Map) {
                Map<String, Object> principal = (Map<String, Object>) authentication.getPrincipal();
                userId = (Long) principal.get("id");
            }

            // 3. 카카오 액세스 토큰 추출 및 카카오 로그아웃
            String kakaoAccessToken = jwtTokenProvider.extractKakaoAccessToken(jwtToken);
            if (kakaoAccessToken != null && !kakaoAccessToken.isEmpty()) {
                boolean logoutSuccess = kakaoService.logoutFromKakao(kakaoAccessToken);
                log.info(logoutSuccess ? "Kakao logout successful!" : "Kakao logout failed!");
            }

            // 4. Redis에서 Refresh Token 삭제
            if (userId != null) {
                jwtTokenProvider.deleteRefreshToken(userId);
                log.info("Refresh token deleted from Redis for user: {}", userId);
            }

            // 5. Access Token 쿠키 만료
            ResponseCookie expiredAccessTokenCookie = ResponseCookie.from("AUTH-TOKEN", "")
                    .httpOnly(true)
                    .maxAge(0)
                    .path("/")
                    .secure(isSecure)
                    .sameSite(sameSiteValue)
                    .build();

            // 6. Refresh Token 쿠키 만료
            ResponseCookie expiredRefreshTokenCookie = ResponseCookie.from("REFRESH-TOKEN", "")
                    .httpOnly(true)
                    .maxAge(0)
                    .path("/")
                    .secure(isSecure)
                    .sameSite(sameSiteValue)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, expiredAccessTokenCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, expiredRefreshTokenCookie.toString());

            // 7. SecurityContext 초기화
            SecurityContextHolder.clearContext();

            return ResponseEntity.ok(Map.of("message", "Logout successful"));

        } catch (Exception e) {
            log.error("Error during Kakao logout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Logout failed: " + e.getMessage());
        }
    }
}
