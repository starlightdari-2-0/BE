package com.example.startlight.constellation.entity;

public enum AnimalCategory {
    DOG("강아지"),
    CAT("고양이"),
    FISH("어류"),
    BIRD("조류"),
    REPTILE("파충류"),
    SMALL_ANIMAL("소동물"),
    OTHER("그 외");

    private final String korean;

    AnimalCategory(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }
}
