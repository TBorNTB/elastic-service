package com.sejong.elasticservice.news.repository;

import com.sejong.elasticservice.news.domain.NewsEvent;
import java.util.List;

public interface NewsRepository {
    String save(NewsEvent newsEvent);
    void deleteById(String newsId);
    List<NewsEvent> searchByTitle(String keyword);
    List<NewsEvent> searchByTags(List<String> tags);
    void updateLikeCount(Long newsId, Long likeCount);
    void updateViewCount(Long newsId, Long viewCount);
}
