package com.example.startlight.global.config;

import com.example.startlight.infra.kakao.dto.KakaoUserInfoResponseDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//token provider
@Component
public class JWTUtils {
    private static final Logger logger = LoggerFactory.getLogger(JWTUtils.class);

    // Access Token: 짧은 시간 (15분 ~ 1시간)
    private static final Duration ACCESS_TOKEN_VALIDITY = Duration.ofHours(1);

    // Refresh Token: 긴 시간 (7일 ~ 30일)
    private static final Duration REFRESH_TOKEN_VALIDITY = Duration.ofDays(14);
    private static final Duration REFRESH_TOKEN_VALIDITY_REMEMBER = Duration.ofDays(30);

    private final Key key;
    private final RedisTemplate<String, String> redisTemplate;

    public JWTUtils(
            @Value("${app.jwtSecret}") String secret,
            RedisTemplate<String, String> redisTemplate) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.redisTemplate = redisTemplate;
    }

    /**
     * Access Token과 Refresh Token을 함께 생성
     */
    public TokenDto createTokens(KakaoUserInfoResponseDto userInfoResponseDto,
                                 String kakaoAccessToken,
                                 boolean rememberMe) {
        String accessToken = createAccessToken(userInfoResponseDto, kakaoAccessToken);
        String refreshToken = createRefreshToken(userInfoResponseDto.getId(), rememberMe);

        return new TokenDto(accessToken, refreshToken);
    }

    /**
     * Access Token 생성 (짧은 만료 시간)
     */
    private String createAccessToken(KakaoUserInfoResponseDto userInfoResponseDto,
                                     String kakaoAccessToken) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + ACCESS_TOKEN_VALIDITY.toMillis());

        return Jwts.builder()
                .setSubject(String.valueOf(userInfoResponseDto.getId()))
                .claim("id", userInfoResponseDto.getId())
                .claim("nickname", userInfoResponseDto.getKakaoAccount().profile.nickName)
                .claim("kakaoAccessToken", kakaoAccessToken)
                .claim("tokenType", "ACCESS")
                .setIssuedAt(new Date(now))
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Refresh Token 생성 및 Redis 저장
     */
    private String createRefreshToken(Long userId, boolean rememberMe) {
        long now = (new Date()).getTime();
        Duration validity = rememberMe ? REFRESH_TOKEN_VALIDITY_REMEMBER : REFRESH_TOKEN_VALIDITY;
        Date expirationDate = new Date(now + validity.toMillis());

        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("tokenType", "REFRESH")
                .setIssuedAt(new Date(now))
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        // Redis에 저장: key = "RT:{userId}", value = refreshToken
        String redisKey = "RT:" + userId;
        redisTemplate.opsForValue().set(
                redisKey,
                refreshToken,
                validity.toMillis(),
                TimeUnit.MILLISECONDS
        );

        logger.info("Refresh token stored in Redis for user: {}", userId);
        return refreshToken;
    }

//    public String createToken(KakaoUserInfoResponseDto userInfoResponseDto, String kakaoAccessToken, boolean rememberMe) {
//        long now = (new Date()).getTime();
//
//        Date validity = new Date(now + (rememberMe ? TOKEN_VALIDITY_REMEMBER.toMillis() : TOKEN_VALIDITY.toMillis()));
//
//        return Jwts.builder()
//                .setSubject(String.valueOf(userInfoResponseDto.getId())) // id를 Subject로 설정
//                .claim("id", userInfoResponseDto.getId()) // id 클레임 추가
//                .claim("nickname", userInfoResponseDto.getKakaoAccount().profile.nickName) // 추가 정보 예시
//                .claim("kakaoAccessToken", kakaoAccessToken)
//                .setIssuedAt(new Date(now))
//                .setExpiration(validity)
//                .signWith(key, SignatureAlgorithm.HS512)
//                .compact();
//    }

    public Authentication verifyAndGetAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .setAllowedClockSkewSeconds(60)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String tokenType = claims.get("tokenType", String.class);
            if (!"ACCESS".equals(tokenType)) {
                logger.error("Invalid token type: {}", tokenType);
                return null;
            }

            // JWT에서 id 클레임 추출
            Long id = claims.get("id",Long.class);
            String nickname = claims.get("nickname", String.class);

            // Principal로 사용자 정보를 Map 형태로 설정
            Map<String, Object> principal = new HashMap<>();
            principal.put("id", id);
            principal.put("nickname", nickname);

            // 권한 정보 가져오기
            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(claims.get("role", String.class));

            // Authentication 객체 생성 시 id 포함
            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("JWT token verification failed", e);
            return null;
        }
    }

    /**
     * Refresh Token으로 새로운 Access Token 발급
     */
    public String refreshAccessToken(String refreshToken, String kakaoAccessToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .setAllowedClockSkewSeconds(60)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            // tokenType 확인
            String tokenType = claims.get("tokenType", String.class);
            if (!"REFRESH".equals(tokenType)) {
                throw new RuntimeException("Invalid token type");
            }

            Long userId = Long.parseLong(claims.getSubject());
            String redisKey = "RT:" + userId;

            // Redis에서 저장된 Refresh Token과 비교
            String storedRefreshToken = redisTemplate.opsForValue().get(redisKey);
            if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
                throw new RuntimeException("Invalid refresh token");
            }

            // 새로운 Access Token 발급
            long now = (new Date()).getTime();
            Date validity = new Date(now + ACCESS_TOKEN_VALIDITY.toMillis());

            return Jwts.builder()
                    .setSubject(String.valueOf(userId))
                    .claim("id", userId)
                    .claim("kakaoAccessToken", kakaoAccessToken)
                    .claim("tokenType", "ACCESS")
                    .setIssuedAt(new Date(now))
                    .setExpiration(validity)
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();

        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Refresh token expired", e);
        } catch (JwtException e) {
            throw new RuntimeException("Invalid refresh token", e);
        }
    }

    /**
     * 로그아웃 시 Refresh Token 삭제
     */
    public void deleteRefreshToken(Long userId) {
        String redisKey = "RT:" + userId;
        redisTemplate.delete(redisKey);
        logger.info("Refresh token deleted for user: {}", userId);
    }

    /**
     * Refresh Token 유효성 검증
     */
    public boolean validateRefreshToken(String refreshToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .setAllowedClockSkewSeconds(60)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            Long userId = Long.parseLong(claims.getSubject());
            String redisKey = "RT:" + userId;
            String storedToken = redisTemplate.opsForValue().get(redisKey);

            return storedToken != null && storedToken.equals(refreshToken);
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractKakaoAccessToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .setAllowedClockSkewSeconds(60)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            if (claims.getExpiration().before(new Date())) {
                throw new RuntimeException("Token expired");
            }
            return claims.get("kakaoAccessToken", String.class);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired", e);
        } catch (JwtException e) {
            throw new RuntimeException("Invalid token", e);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing token", e);
        }
    }

    public static class TokenDto {
        private String accessToken;
        private String refreshToken;

        public TokenDto(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }
}
