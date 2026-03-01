package com.sejong.elasticservice.domain.project.repository;


import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.domain.project.domain.PostSortType;
import com.sejong.elasticservice.domain.project.domain.ProjectStatus;
import com.sejong.elasticservice.domain.project.domain.ProjectDocument;
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
    public String save(ProjectDocument projectDocument) {
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
    public PageResponse<ProjectDocument> searchProjects(String query, ProjectStatus projectStatus, List<String> categories, List<String> techStacks, PostSortType postSortType, int size, int page) {

        Query textQuery = (query == null || query.isBlank())
                ? MatchAllQuery.of(m -> m)._toQuery()
                : MultiMatchQuery.of(m -> m
                .query(query)
                .fields("title^3", "description^1")
                .fuzziness("AUTO")
        )._toQuery();

        List<Query> filters = new ArrayList<>();
        if (projectStatus != null) {
            Query projectStatusFilter = TermQuery.of(t -> t
                    .field("projectStatus")
                    .value(projectStatus.name())
            )._toQuery();
            filters.add(projectStatusFilter);
        }

        List<String> categoryList = categories == null ? List.of() : categories;
        List<String> validCategories = categoryList.stream()
                .filter(c -> c != null && !c.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
        if (!validCategories.isEmpty()) {
            List<FieldValue> categoryValues = validCategories.stream()
                    .map(FieldValue::of)
                    .toList();
            Query projectCategoriesFilter = TermsQuery.of(t -> t
                    .field("projectCategories")
                    .terms(v -> v.value(categoryValues))
            )._toQuery();
            filters.add(projectCategoriesFilter);
        }

        List<String> techStackList = techStacks == null ? List.of() : techStacks;
        List<String> validTechStacks = techStackList.stream()
                .filter(t -> t != null && !t.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
        if (!validTechStacks.isEmpty()) {
            List<FieldValue> techStackValues = validTechStacks.stream()
                    .map(FieldValue::of)
                    .toList();
            Query projectTechStacksFilter = TermsQuery.of(t -> t
                    .field("projectTechStacks")
                    .terms(v -> v.value(techStackValues))
            )._toQuery();
            filters.add(projectTechStacksFilter);
        }

        Query boolQuery = BoolQuery.of(b -> b
                .must(textQuery)
                .filter(filters)
        )._toQuery();

        Sort sort = switch (postSortType) {
            case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case POPULAR -> Sort.by(Sort.Direction.DESC, "likeCount");
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

    @Override
    public PageResponse<ProjectDocument> searchByMemberName(String name, int size, int page) {
        Query boolQuery = BoolQuery.of(b -> b
                .should(
                        TermQuery.of(t -> t.field("owner.nickname").value(name))._toQuery(),
                        TermQuery.of(t -> t.field("owner.realname").value(name))._toQuery(),
                        TermQuery.of(t -> t.field("collaborators.nickname").value(name))._toQuery(),
                        TermQuery.of(t -> t.field("collaborators.realname").value(name))._toQuery()
                )
                .minimumShouldMatch("1")
        )._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withSort(Sort.by(Sort.Direction.DESC, "createdAt"))
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
    public PageResponse<ProjectDocument> searchByUsername(String username, int size, int page) {
        Query boolQuery = BoolQuery.of(b -> b
                .should(
                        TermQuery.of(t -> t.field("owner.username").value(username))._toQuery(),
                        TermQuery.of(t -> t.field("collaborators.username").value(username))._toQuery()
                )
                .minimumShouldMatch("1")
        )._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withSort(Sort.by(Sort.Direction.DESC, "createdAt"))
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
}
