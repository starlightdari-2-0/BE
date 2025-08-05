package com.example.startlight.pet.entity;

import lombok.Getter;

@Getter
public enum Personality {
    CHARMING("사교적이고 얘교 많음"),
    INDEPENDENT("독립적이고 자유로움"),
    CURIOUS("호기심 많고 모험심 강함"),
    CALM("순하고 조용함"),
    STUBBORN("고집 세고 자기주장이 강함"),
    SENSITIVE("예민하고 감수성이 풍부함");

    private final String description;

    Personality(String description) {
        this.description = description;
    }

}
