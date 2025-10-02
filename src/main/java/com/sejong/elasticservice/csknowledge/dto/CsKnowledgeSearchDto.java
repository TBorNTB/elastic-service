package com.sejong.elasticservice.csknowledge.dto;

import com.sejong.elasticservice.csknowledge.domain.CsKnowledgeDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CsKnowledgeSearchDto {
    private String id;
    private String title;
    private String content;
    private String category;
    private String createdAt;
    private long likeCount;
    private long viewCount;

    public static CsKnowledgeSearchDto toCsKnowledgeSearchDto(CsKnowledgeDocument document) {
        return CsKnowledgeSearchDto.builder()
                .id(document.getId())
                .title(document.getTitle())
                .content(document.getContent())
                .category(document.getCategory())
                .createdAt(document.getCreatedAt())
                .likeCount(document.getLikeCount())
                .viewCount(document.getViewCount())
                .build();
    }
}