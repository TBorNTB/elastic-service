package com.sejong.elasticservice.domain.news.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsEvent {
    private String id;
    private Content content;
    private String thumbnailPath;
    private String writerId;
    private List<String> participantIds;
    private List<String> tags;
    private String createdAt;
    private String updatedAt;
    private long likeCount;
    private long viewCount;
}