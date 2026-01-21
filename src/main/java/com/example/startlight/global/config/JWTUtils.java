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

//token provider
@Component
public class JWTUtils {
    private static final Logger logger = LoggerFactory.getLogger(JWTUtils.class);

    private static final Duration TOKEN_VALIDITY = Duration.ofDays(1); // 1일
    private static final Duration TOKEN_VALIDITY_REMEMBER = Duration.ofDays(30); // 30一
    private final Key key;
    private final RedisTemplate<String, String> redisTemplate;

    public JWTUtils(
            @Value("${app.jwtSecret}") String secret,
            RedisTemplate<String, String> redisTemplate) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.redisTemplate = redisTemplate;
    }

    public String createToken(KakaoUserInfoResponseDto userInfoResponseDto, String kakaoAccessToken, boolean rememberMe) {
        long now = (new Date()).getTime();

        Date validity = new Date(now + (rememberMe ? TOKEN_VALIDITY_REMEMBER.toMillis() : TOKEN_VALIDITY.toMillis()));

        return Jwts.builder()
                .setSubject(String.valueOf(userInfoResponseDto.getId())) // id를 Subject로 설정
                .claim("id", userInfoResponseDto.getId()) // id 클레임 추가
                .claim("nickname", userInfoResponseDto.getKakaoAccount().profile.nickName) // 추가 정보 예시
                .claim("kakaoAccessToken", kakaoAccessToken)
                .setIssuedAt(new Date(now))
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Authentication verifyAndGetAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .setAllowedClockSkewSeconds(60) // ✅ 허용 시간 오차 설정 (60초)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

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

    public String extractKakaoAccessToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .setAllowedClockSkewSeconds(60) // ✅ 허용 시간 오차 설정 (60초)
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
}
