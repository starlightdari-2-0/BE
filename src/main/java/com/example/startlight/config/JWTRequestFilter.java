package com.example.startlight.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class JWTRequestFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JWTRequestFilter.class);
    private final JWTUtils jwtUtils;

    @Autowired
    public JWTRequestFilter(JWTUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        logger.debug("Entering JWTRequestFilter");
        logger.debug(response.toString());
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            // g_state 쿠키 필터링 및 제거
            cookies = Arrays.stream(cookies)
                    .filter(cookie -> !cookie.getName().equals("g_state"))  // g_state 쿠키 제외
                    .toArray(Cookie[]::new);

            logger.info("Cookies after filtering: " + Arrays.toString(cookies));

            Cookie authCookie = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("AUTH-TOKEN"))
                    .findAny().orElse(null);
            if (authCookie != null) {
                logger.info("Auth Cookie found: " + authCookie.getValue());
                Authentication authentication = jwtUtils.verifyAndGetAuthentication(authCookie.getValue());
                if (authentication != null) {
                    logger.info("Authentication successful: " + authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    logger.info("Authentication failed");
                }
            } else {
                logger.info("Auth Cookie not found");
            }
        } else {
            logger.debug("No cookies found");
        }
        logger.debug("Exiting JWTRequestFilter");
        filterChain.doFilter(request, response);
    }
}
