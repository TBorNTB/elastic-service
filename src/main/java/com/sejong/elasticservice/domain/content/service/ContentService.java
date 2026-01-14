package com.sejong.elasticservice.domain.content.service;

import com.sejong.elasticservice.common.dto.UserInfo;
import com.sejong.elasticservice.domain.content.dto.PostRequest;
import com.sejong.elasticservice.domain.content.dto.PostSummaryDto;
import com.sejong.elasticservice.domain.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.domain.csknowledge.repository.CsKnowledgeDocumentRepository;
import com.sejong.elasticservice.domain.news.domain.NewsDocument;
import com.sejong.elasticservice.domain.news.repository.NewsDocumentRepository;
import com.sejong.elasticservice.domain.postlike.domain.PostType;
import com.sejong.elasticservice.domain.project.domain.ProjectDocument;
import com.sejong.elasticservice.domain.project.repository.ProjectElasticDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final ProjectElasticDocumentRepository projectRepository;
    private final NewsDocumentRepository newsRepository;
    private final CsKnowledgeDocumentRepository csKnowledgeRepository;

    public List<PostSummaryDto> getPostsByTypeAndIds(PostRequest request) {
        List<PostRequest.PostItem> posts = request.getPosts();
        
        // postType별로 그룹화
        Map<PostType, List<Long>> groupedByType = posts.stream()
                .collect(Collectors.groupingBy(
                        PostRequest.PostItem::getPostType,
                        Collectors.mapping(PostRequest.PostItem::getPostId, Collectors.toList())
                ));
        
        List<PostSummaryDto> result = new ArrayList<>();
        
        // PROJECT 처리
        if (groupedByType.containsKey(PostType.PROJECT)) {
            List<String> projectIds = groupedByType.get(PostType.PROJECT).stream()
                    .map(String::valueOf)
                    .toList();
            Iterable<ProjectDocument> documents = projectRepository.findAllById(projectIds);
            List<ProjectDocument> documentList = new ArrayList<>();
            documents.forEach(documentList::add);
            result.addAll(documentList.stream()
                    .map(doc -> PostSummaryDto.builder()
                            .postId(Long.parseLong(doc.getId()))
                            .postType(PostType.PROJECT)
                            .title(doc.getTitle())
                            .createdAt(doc.getCreatedAt())
                            .writer(UserInfo.from(doc.getOwner()))
                            .build())
                    .toList());
        }
        
        // NEWS 처리
        if (groupedByType.containsKey(PostType.NEWS)) {
            List<String> newsIds = groupedByType.get(PostType.NEWS).stream()
                    .map(String::valueOf)
                    .toList();
            Iterable<NewsDocument> documents = newsRepository.findAllById(newsIds);
            List<NewsDocument> documentList = new ArrayList<>();
            documents.forEach(documentList::add);
            result.addAll(documentList.stream()
                    .map(doc -> PostSummaryDto.builder()
                            .postId(Long.parseLong(doc.getId()))
                            .postType(PostType.NEWS)
                            .title(doc.getContent() != null ? doc.getContent().getTitle() : null)
                            .createdAt(doc.getCreatedAt())
                            .writer(UserInfo.from(doc.getWriter()))
                            .build())
                    .toList());
        }
        
        // CSKNOWLEDGE 처리
        if (groupedByType.containsKey(PostType.ARTICLE)) {
            List<String> csIds = groupedByType.get(PostType.ARTICLE).stream()
                    .map(String::valueOf)
                    .toList();
            Iterable<CsKnowledgeDocument> documents = csKnowledgeRepository.findAllById(csIds);
            List<CsKnowledgeDocument> documentList = new ArrayList<>();
            documents.forEach(documentList::add);
            result.addAll(documentList.stream()
                    .map(doc -> PostSummaryDto.builder()
                            .postId(Long.parseLong(doc.getId()))
                            .postType(PostType.ARTICLE)
                            .title(doc.getTitle())
                            .createdAt(doc.getCreatedAt())
                            .writer(UserInfo.from(doc.getWriter()))
                            .build())
                    .toList());
        }
        
        return result;
    }
}
