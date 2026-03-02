package com.sejong.elasticservice.domain.content.dto;

import com.sejong.elasticservice.common.dto.UserInfo;
import com.sejong.elasticservice.domain.postlike.domain.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSummaryDto {
    private Long postId;
    private PostType postType;
    private String title;
    private String thumbnailUrl;
    private String createdAt;
    private UserInfo writer;
}
