package com.sejong.elasticservice.news.service;

import com.sejong.elasticservice.news.dto.NewsSearchDto;
import com.sejong.elasticservice.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public List<NewsSearchDto> searchNews(String keyword, String category, int page, int size) {
        return newsRepository.searchNews(keyword, category, page, size);
    }

    public List<NewsSearchDto> searchByTags(List<String> tags, int page, int size) {
        return newsRepository.searchByTags(tags, page, size);
    }
}