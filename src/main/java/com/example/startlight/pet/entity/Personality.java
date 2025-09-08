package com.example.startlight.pet.entity;

import lombok.Getter;

@Getter
public enum Personality {
    ACTIVE("활발한"),
    GENTLE("순한"),
    CALM("차분한"),
    INTROVERT("내향적인"),
    COOL("쿨한"),
    LOVELY("사랑스러운"),
    AFFECTIONATE("애교많은"),
    TIMID("겁많은");

    private final String description;

    Personality(String description) {
        this.description = description;
    }

}
