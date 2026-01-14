package com.sejong.elasticservice.domain.content.dto;

import com.sejong.elasticservice.domain.postlike.domain.PostType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
    private List<PostItem> posts;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostItem {
        private PostType postType;
        private Long postId;
    }
}