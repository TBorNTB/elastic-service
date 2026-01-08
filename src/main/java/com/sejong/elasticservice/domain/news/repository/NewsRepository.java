package com.sejong.elasticservice.domain.news.repository;

import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.domain.news.domain.NewsDocument;
import com.sejong.elasticservice.domain.news.domain.NewsEvent;
import com.sejong.elasticservice.domain.project.domain.PostSortType;

import java.util.List;

public interface NewsRepository {
    String save(NewsEvent newsEvent);
    void deleteById(String newsId);
    PageResponse<NewsDocument> searchNews(String keyword, String category, PostSortType postSortType, int page, int size);
    List<NewsDocument> searchNews(int page, int size);
    List<NewsDocument> searchByTags(List<String> tags, int page, int size);
    void updateLikeCount(Long newsId, Long likeCount);
    void updateViewCount(Long newsId, Long viewCount);

    List<String> getSuggestions(String query);
}
