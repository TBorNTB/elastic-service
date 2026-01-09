package com.sejong.elasticservice.domain.news.dto;

import com.sejong.elasticservice.common.embedded.Names;
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

    public static NewsSearchDto toNewsSearchDto(NewsDocument nd) {
        return NewsSearchDto.builder()
                .id(Long.parseLong(nd.getId()))
                .content(nd.getContent())
                .thumbnailPath(nd.getThumbnailPath())
                .writerId(nd.getWriter().getNickname())
                .participantIds(nd.getParticipants().stream().map(Names::getNickname).toList())
                .tags(nd.getTags())
                .createdAt(nd.getCreatedAt())
                .updatedAt(nd.getUpdatedAt())
                .likeCount(nd.getLikeCount())
                .viewCount(nd.getViewCount())
                .build();
    }
}