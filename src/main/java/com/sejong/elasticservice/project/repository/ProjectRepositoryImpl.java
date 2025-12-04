package com.sejong.elasticservice.project.repository;


import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.project.domain.ProjectEvent;
import com.sejong.elasticservice.project.domain.ProjectSortType;
import com.sejong.elasticservice.project.domain.ProjectStatus;
import com.sejong.elasticservice.project.domain.ProjectDocument;
import com.sejong.elasticservice.project.dto.ProjectSearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository {

    private final ProjectElasticDocumentRepository repository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public String save(ProjectEvent project) {
        ProjectDocument projectDocument = ProjectDocument.from(project);
        ProjectDocument savedProjectDocument = repository.save(projectDocument);
        return savedProjectDocument.getId();
    }

    @Override
    public void deleteById(String projectId) {
        repository.deleteById(projectId);
    }

    @Override
    public List<String> getSuggestions(String query) {
        Query multiMatchQuery = MultiMatchQuery.of(m -> m
                .query(query)
                .type(TextQueryType.BoolPrefix)
                .fields("title.auto_complete", "title.auto_complete._2gram",
                        "title.auto_complete._3gram"))._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(multiMatchQuery)
                .withPageable(PageRequest.of(0, 5))
                .build();

        SearchHits<ProjectDocument> searchHits = elasticsearchOperations.search(nativeQuery, ProjectDocument.class);
        return searchHits.getSearchHits().stream()
                .map(hit -> {
                    ProjectDocument projectDocument = hit.getContent();
                    return projectDocument.getTitle();
                })
                .toList();
    }

    @Override
    public PageResponse<ProjectDocument> searchProjects(String query, ProjectStatus projectStatus, List<String> categories, List<String> techStacks, ProjectSortType projectSortType , int size, int page) {

        Query textQuery = (query == null || query.isBlank())
                ? MatchAllQuery.of(m -> m)._toQuery()
                : MultiMatchQuery.of(m -> m
                .query(query)
                .fields("title^3", "description^1")
                .fuzziness("AUTO")
        )._toQuery();

        Query projectStatusFilter = TermQuery.of(t -> t
                .field("projectStatus")
                .value(projectStatus.name())
        )._toQuery();

        List<FieldValue> categoryValues = categories.stream()
                .map(FieldValue::of)
                .toList();

        Query projectCategoriesFilter = TermsQuery.of(t -> t
                .field("projectCategories")
                .terms(v -> v.value(categoryValues))
        )._toQuery();

        List<FieldValue> techStackValues = techStacks.stream()
                .map(FieldValue::of)
                .toList();

        Query projectTechStacksFilter = TermsQuery.of(t -> t
                .field("projectTechStacks")
                .terms(v -> v.value(techStackValues))
        )._toQuery();

        List<Query> filters = new ArrayList<>();
        if (projectStatus != null && !projectStatus.name().isBlank()) {
            filters.add(projectStatusFilter);
        }
        if (!categories.isEmpty()) filters.add(projectCategoriesFilter);
        if (!techStacks.isEmpty()) filters.add(projectTechStacksFilter);

        Query boolQuery = BoolQuery.of(b -> b
                .must(textQuery)
                .filter(filters)
        )._toQuery();

        Sort sort = switch (projectSortType) {
            case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case POPULAR -> Sort.by(Sort.Direction.DESC, "likeCount");
            case NAME -> Sort.by(Sort.Direction.ASC, "title.keyword");
        };

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withSort(sort)
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<ProjectDocument> searchHits = elasticsearchOperations.search(
                nativeQuery,
                ProjectDocument.class
        );

        SearchPage<ProjectDocument> searchPage = SearchHitSupport.searchPageFor(searchHits, PageRequest.of(page, size));

        return new PageResponse<>(
                searchPage.getContent().stream()
                        .map(SearchHit::getContent)
                        .toList(),
                searchPage.getNumber(),
                searchPage.getSize(),
                searchPage.getTotalElements(),
                searchPage.getTotalPages()
        );
    }

    @Override
    public List<ProjectDocument> searchProjects(int size, int page) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withSort(Sort.by(Sort.Order.desc("createdAt")))
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<ProjectDocument> searchHits = elasticsearchOperations.search(nativeQuery, ProjectDocument.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .toList();
    }

    @Override
    public void updateLikeCount(Long postId, Long likeCount) {
        Document patch = Document.create();
        patch.put("likeCount",likeCount);

        UpdateQuery uq = UpdateQuery.builder(postId.toString())
                .withDocument(patch)
                .build();

        IndexCoordinates index = elasticsearchOperations.getIndexCoordinatesFor(ProjectDocument.class);
        elasticsearchOperations.update(uq, index);
    }

    @Override
    public void updateViewCount(Long postId, Long viewCount) {
        Document patch = Document.create();
        patch.put("viewCount",viewCount);

        UpdateQuery uq = UpdateQuery.builder(postId.toString())
                .withDocument(patch)
                .build();

        IndexCoordinates index = elasticsearchOperations.getIndexCoordinatesFor(ProjectDocument.class);
        elasticsearchOperations.update(uq, index);
    }

}
