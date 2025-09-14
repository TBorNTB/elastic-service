package com.sejong.elasticservice.news.domain;

import java.util.Arrays;

public enum NewsCategory {

    MT("MT"),
    OT("OT"),
    STUDY("스터디"),
    SEMINAR("세미나"),
    UNITED_SEMINAR("연합 세미나"),
    CONFERENCE("컨퍼런스"),
    CTF("CTF");

    private final String description;

    NewsCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static NewsCategory of(String description) {
        return Arrays.stream(NewsCategory.values())
                .filter(c -> c.description.equals(description))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
    }
}