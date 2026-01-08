package com.sejong.elasticservice.domain.news.dto;

import com.sejong.elasticservice.domain.news.domain.Content;
import com.sejong.elasticservice.domain.news.domain.NewsDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsSearchDto {
    private Long id;
    private Content content;
    private String thumbnailPath;
    private String writerId;
    private List<String> participantIds;
    private List<String> tags;
    private String createdAt;
    private String updatedAt;
    private long likeCount;
    private long viewCount;

    public static NewsSearchDto toNewsSearchDto(NewsDocument document) {
        return NewsSearchDto.builder()
                .id(Long.parseLong(document.getId()))
                .content(document.getContent())
                .thumbnailPath(document.getThumbnailPath())
                .writerId(document.getWriterId())
                .participantIds(document.getParticipantIds())
                .tags(document.getTags())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .likeCount(document.getLikeCount())
                .viewCount(document.getViewCount())
                .build();
    }
}