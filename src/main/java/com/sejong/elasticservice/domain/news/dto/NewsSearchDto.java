package com.sejong.elasticservice.domain.news.dto;

import com.sejong.elasticservice.domain.UserInfo;
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
    private String thumbnailUrl;
    private List<String> tags;
    private String createdAt;
    private String updatedAt;
    private long likeCount;
    private long viewCount;
    
    private UserInfo writer;
    private List<UserInfo> participants;

    public static NewsSearchDto toNewsSearchDto(NewsDocument nd) {
        return NewsSearchDto.builder()
                .id(Long.parseLong(nd.getId()))
                .content(nd.getContent())
                .thumbnailUrl(nd.getThumbnailUrl())
                .tags(nd.getTags())
                .createdAt(nd.getCreatedAt())
                .updatedAt(nd.getUpdatedAt())
                .likeCount(nd.getLikeCount())
                .viewCount(nd.getViewCount())
                .writer(UserInfo.from(nd.getWriter()))
                .participants(nd.getParticipants() != null 
                        ? nd.getParticipants().stream().map(UserInfo::from).toList() 
                        : List.of())
                .build();
    }
}