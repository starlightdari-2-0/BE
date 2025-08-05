package com.example.startlight.memoryStar.entity;

public enum EmotionCtg {
    HAPPY("행복"),
    TOUCHED("감동"),
    PEACEFUL("안정/평화"),
    SAD("슬픔"),
    GRATEFUL("감사"),
    SURPRISED("놀람"),
    REGRET("아쉬움"),
    LOVE("사랑"),
    EXPECTATION("기대감");

    private final String displayName;

    EmotionCtg(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
