package com.sejong.elasticservice.domain.csknowledge.dto;

import com.sejong.elasticservice.common.dto.UserInfo;
import com.sejong.elasticservice.domain.csknowledge.domain.CsKnowledgeDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CsKnowledgeSearchDto {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String createdAt;
    private String thumbnailUrl;
    private long likeCount;
    private long viewCount;
    
    private UserInfo writer;

    public static CsKnowledgeSearchDto toCsKnowledgeSearchDto(CsKnowledgeDocument document) {
        return CsKnowledgeSearchDto.builder()
                .id(Long.parseLong(document.getId()))
                .title(document.getTitle())
                .content(document.getContent())
                .category(document.getCategory())
                .thumbnailUrl(document.getThumbnailUrl())
                .createdAt(document.getCreatedAt())
                .likeCount(document.getLikeCount())
                .viewCount(document.getViewCount())
                .writer(UserInfo.from(document.getWriter()))
                .build();
    }
}