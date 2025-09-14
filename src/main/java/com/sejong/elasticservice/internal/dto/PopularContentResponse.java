package com.sejong.elasticservice.internal.dto;

import com.sejong.elasticservice.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.news.domain.NewsDocument;
import com.sejong.elasticservice.project.domain.ProjectDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PopularContentResponse {
    private String id;
    private String contentType; // PROJECT, NEWS, CS-KNOWLEDGE

    private String title;
    private String content;
    private String category; // 카테고리 정보

    private String createdAt;
    private long likeCount;
    private long viewCount;

    public static PopularContentResponse fromProject(ProjectDocument project) {
        return PopularContentResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .content(project.getDescription())
                .contentType("PROJECT")
                .category(String.join(", ", project.getProjectCategories()))
                .createdAt(project.getCreatedAt())
                .likeCount(project.getLikeCount())
                .viewCount(project.getViewCount())
                .build();
    }

    public static PopularContentResponse fromNews(NewsDocument newsItem) {
        String category = newsItem.getContent() != null && newsItem.getContent().getCategory() != null
                ? newsItem.getContent().getCategory().name() : null;
        return PopularContentResponse.builder()
                .id(newsItem.getId())
                .title(newsItem.getContent().getTitle())
                .content(newsItem.getContent().getSummary())
                .contentType("NEWS")
                .category(category)
                .createdAt(newsItem.getCreatedAt())
                .likeCount(newsItem.getLikeCount())
                .viewCount(newsItem.getViewCount())
                .build();
    }

    public static PopularContentResponse fromCsKnowledge(CsKnowledgeDocument cs) {
        return PopularContentResponse.builder()
                .id(cs.getId())
                .title(cs.getTitle())
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