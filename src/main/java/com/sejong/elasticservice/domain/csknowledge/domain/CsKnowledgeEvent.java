package com.sejong.elasticservice.domain.csknowledge.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsKnowledgeEvent {
    private String id;
    private String title;
    private String content;
    private String writerId;
    private String category;
    private String createdAt;
    private long likeCount;
    private long viewCount;
}