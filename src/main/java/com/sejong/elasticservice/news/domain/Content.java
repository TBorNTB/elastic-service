package com.sejong.elasticservice.news.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Content {
    private String title;
    private String summary;
    private String content;
    private NewsCategory category;
}