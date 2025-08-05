package com.example.startlight.memoryStar.entity;

public enum ActivityCtg {
    WALK("산책"),
    PLAY("놀이"),
    TRAINING("훈련"),
    FOOD("먹이/간식"),
    HOSPITAL("병원"),
    GROOMING("목욕/미용"),
    TRAVEL("여행"),
    ANNIVERSARY("기념일"),
    RELAX("쉬는 시간");

    private final String displayName;

    ActivityCtg(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
