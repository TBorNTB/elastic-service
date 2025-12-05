package com.sejong.elasticservice.news.service;

import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.news.domain.NewsDocument;
import com.sejong.elasticservice.news.dto.NewsSearchDto;
import com.sejong.elasticservice.news.repository.NewsRepository;
import com.sejong.elasticservice.project.domain.ProjectSortType;
import com.sejong.elasticservice.project.dto.ProjectSearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public PageResponse<NewsSearchDto> searchNews(String keyword, String category, ProjectSortType projectSortType, int page, int size) {
        PageResponse<NewsDocument> result = newsRepository.searchNews(keyword, category, projectSortType, page, size);
        List<NewsSearchDto> dtoList = result.content()
                .stream()
                .map(NewsSearchDto::toNewsSearchDto)
                .toList();

        return new PageResponse<>(
                dtoList,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    public List<NewsSearchDto> searchNews(int page, int size) {
        List<NewsDocument> newsDocuments = newsRepository.searchNews(page, size);
        return newsDocuments.stream().map(NewsSearchDto::toNewsSearchDto).toList();
    }

    public List<NewsSearchDto> searchByTags(List<String> tags, int page, int size) {
        List<NewsDocument> newsDocuments = newsRepository.searchByTags(tags, page, size);
        return newsDocuments.stream().map(NewsSearchDto::toNewsSearchDto).toList();
    }

    public List<String> getSuggestions(String query) {
        return newsRepository.getSuggestions(query);
    }
}