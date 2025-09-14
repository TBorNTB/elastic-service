package com.sejong.elasticservice.news.repository;

import com.sejong.elasticservice.news.domain.NewsEvent;
import com.sejong.elasticservice.news.dto.NewsSearchDto;
import java.util.List;

public interface NewsRepository {
    String save(NewsEvent newsEvent);
    void deleteById(String newsId);
    List<NewsSearchDto> searchNews(String keyword, String category, int page, int size);
    List<NewsSearchDto> searchByTags(List<String> tags, int page, int size);
    void updateLikeCount(Long newsId, Long likeCount);
    void updateViewCount(Long newsId, Long viewCount);
}
