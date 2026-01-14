package com.sejong.elasticservice.domain.news.service;

import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.domain.news.domain.NewsDocument;
import com.sejong.elasticservice.domain.news.dto.NewsSearchDto;
import com.sejong.elasticservice.domain.news.repository.NewsRepository;
import com.sejong.elasticservice.domain.project.domain.PostSortType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public PageResponse<NewsSearchDto> searchNews(String keyword, String category, PostSortType postSortType, int page, int size) {
        PageResponse<NewsDocument> result = newsRepository.searchNews(keyword, category, postSortType, page, size);
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

    public PageResponse<NewsSearchDto> searchByMemberName(String name, int size, int page) {
        PageResponse<NewsDocument> result = newsRepository.searchByMemberName(name, size, page);

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

    public PageResponse<NewsSearchDto> searchByUsername(String username, int size, int page) {
        PageResponse<NewsDocument> result = newsRepository.searchByUsername(username, size, page);

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
}