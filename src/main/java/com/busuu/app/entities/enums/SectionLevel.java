package com.busuu.app.entities.enums;

public enum SectionLevel {
    NEEDS_PRACTICE(0, 30),
    IMPROVING(30, 70),
    SOLID(70, 90),
    MASTERED(90, 100);

    private final double min;
    private final double max;

    SectionLevel(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static SectionLevel fromProgress(double progress) {
        for (SectionLevel level : values()) {
            if (progress >= level.min && progress < level.max) {
                return level;
            }
        }
        return MASTERED;
    }
}

