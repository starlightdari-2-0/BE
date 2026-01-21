package com.example.startlight.infra.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class KakaoUserCreateDto {
    public Long id;
    public String nickName;
    private String email;
    public String profileImageUrl;
}
