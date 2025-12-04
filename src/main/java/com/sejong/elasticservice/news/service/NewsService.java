package com.sejong.elasticservice.news.service;

import com.sejong.elasticservice.news.domain.NewsDocument;
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
      List<NewsDocument> newsDocuments = newsRepository.searchNews(keyword, category, page, size);
      return newsDocuments.stream().map(NewsSearchDto::toNewsSearchDto).toList();
    }

    public List<NewsSearchDto> searchNews(int page, int size) {
        List<NewsDocument> newsDocuments = newsRepository.searchNews(page, size);
        return newsDocuments.stream().map(NewsSearchDto::toNewsSearchDto).toList();
    }

    public List<NewsSearchDto> searchByTags(List<String> tags, int page, int size) {
      List<NewsDocument> newsDocuments = newsRepository.searchByTags(tags, page, size);
      return newsDocuments.stream().map(NewsSearchDto::toNewsSearchDto).toList();
    }

}