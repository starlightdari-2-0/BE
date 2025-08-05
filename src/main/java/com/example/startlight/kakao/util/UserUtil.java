package com.example.startlight.kakao.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

public class UserUtil {
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Map) {
            Map<String, Object> principal = (Map<String, Object>) authentication.getPrincipal();
            Long id =  (Long) principal.get("id");
            return id;
        }

        throw new IllegalStateException("User is not authenticated");
    }
}
