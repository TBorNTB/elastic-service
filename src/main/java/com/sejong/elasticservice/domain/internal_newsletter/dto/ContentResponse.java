package com.sejong.elasticservice.domain.internal_newsletter.dto;

import com.sejong.elasticservice.domain.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.domain.news.domain.NewsDocument;
import com.sejong.elasticservice.domain.project.domain.ProjectDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentResponse {
    private String id;
    private String contentType; // PROJECT, NEWS, CS-KNOWLEDGE
    private String thumbnailUrl;
    private String title;
    private String content;
    private String category; // 카테고리 정보

    private String createdAt;
    private long likeCount;
    private long viewCount;

    public static ContentResponse fromProject(ProjectDocument project) {
        return ContentResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .content(project.getDescription())
                .thumbnailUrl(project.getThumbnailUrl())
                .contentType("PROJECT")
                .category(String.join(", ", project.getProjectCategories()))
                .createdAt(project.getCreatedAt())
                .likeCount(project.getLikeCount())
                .viewCount(project.getViewCount())
                .build();
    }

    public static ContentResponse fromNews(NewsDocument newsItem) {
        String category = newsItem.getContent() != null ? newsItem.getContent().getCategory() : null;
        return ContentResponse.builder()
                .id(newsItem.getId())
                .title(newsItem.getContent().getTitle())
                .thumbnailUrl(newsItem.getThumbnailUrl())
                .content(newsItem.getContent().getSummary())
                .contentType("NEWS")
                .category(category)
                .createdAt(newsItem.getCreatedAt())
                .likeCount(newsItem.getLikeCount())
                .viewCount(newsItem.getViewCount())
                .build();
    }

    public static ContentResponse fromCsKnowledge(CsKnowledgeDocument cs) {
        return ContentResponse.builder()
                .id(cs.getId())
                .title(cs.getTitle())
                .thumbnailUrl(cs.getThumbnailUrl())
                .content(cs.getContent())
                .contentType("CS-KNOWLEDGE")
                .category(cs.getCategory())
                .createdAt(cs.getCreatedAt())
                .likeCount(cs.getLikeCount())
                .viewCount(cs.getViewCount())
                .build();
    }

    // 인기도 점수 계산을 위한 헬퍼 메서드
    public double calculatePopularityScore() {
        return likeCount * 2.0 + viewCount;
    }
}