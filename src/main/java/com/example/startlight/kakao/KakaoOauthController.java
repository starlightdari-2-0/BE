package com.example.startlight.kakao;

import com.example.startlight.config.JWTUtils;
import com.example.startlight.kakao.dto.KakaoUserCreateDto;
import com.example.startlight.kakao.dto.KakaoUserInfoResponseDto;
import com.example.startlight.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
            log.info("accessToken: {}", accessToken);

            // 2. 사용자 정보 가져오기
            KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);
            log.info("Kakao User Info: {}", userInfo);

            // 3. JWT 생성
            boolean rememberMe = false; // 예: 클라이언트 요청에 따라 결정
            String jwtToken = jwtTokenProvider.createToken(userInfo, accessToken, rememberMe);
            System.out.println(jwtToken);

            // 5. 인증 객체 확인 (로그 추가)
            Authentication authentication = jwtTokenProvider.verifyAndGetAuthentication(jwtToken);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            boolean isSecure = request.getScheme().equals("https");
            String sameSiteValue = isSecure ? "None" : "Lax";

            final ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", jwtToken)
                    .httpOnly(true)
                    .maxAge(7 * 24 * 3600)
                    .path("/")
                    .secure(isSecure)
                    .sameSite(sameSiteValue)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            response.setHeader("Access-Control-Expose-Headers", "Set-Cookie");

            log.info(authentication.getPrincipal().toString());

            KakaoUserCreateDto kakaoUserCreateDto = KakaoUserCreateDto.builder().id(userInfo.getId())
                            .nickName(userInfo.getKakaoAccount().profile.getNickName())
                    .email(userInfo.getKakaoAccount().email)
                                    .profileImageUrl(userInfo.getKakaoAccount().profile.getProfileImageUrl()).build();

            boolean isFirstLogin = !memberService.isFirstKakaoLogin(userInfo.getId());
            memberService.loginMember(kakaoUserCreateDto);

            Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();

            if (authentication1 != null && authentication1.getPrincipal() instanceof Map) {
                Map<String, Object> principal = (Map<String, Object>) authentication.getPrincipal();
                Long id =  (Long) principal.get("id");
                log.info("Kakao User ID: {}", id);
            }
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

            // 2. JWT 검증 및 카카오 액세스 토큰 추출
            String kakaoAccessToken = jwtTokenProvider.extractKakaoAccessToken(jwtToken);
            if (kakaoAccessToken == null || kakaoAccessToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
            }

            // 3. 카카오 로그아웃 요청
            boolean logoutSuccess = kakaoService.logoutFromKakao(kakaoAccessToken);
            if (!logoutSuccess) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to logout from Kakao.");
            }

            // 4. 쿠키 만료 처리 (클라이언트 측 자동 삭제)
            ResponseCookie expiredCookie = ResponseCookie.from("AUTH-TOKEN", "")
                    .httpOnly(true)  // 보안 강화를 위해 httpOnly 유지
                    .maxAge(0)  // 쿠키 만료
                    .path("/")
                    .secure(isSecure)
                    .sameSite(sameSiteValue)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
            log.info(logoutSuccess ? "Logout successful!" : "Logout failed!");

            return ResponseEntity.ok(Map.of("message", "Logout successful"));

        } catch (Exception e) {
            log.error("Error during Kakao logout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Logout failed: " + e.getMessage());
        }
    }
}
