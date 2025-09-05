package com.sejong.elasticservice.project.repository;


import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.sejong.elasticservice.project.ProjectEvent;
import com.sejong.elasticservice.project.ProjectStatus;
import com.sejong.elasticservice.project.domain.ProjectDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProjectElasticRepositoryImpl implements ProjectElasticRepository {

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
    public List<ProjectEvent> searchProjects(String query, ProjectStatus projectStatus, List<String> categories, List<String> techStacks, int size, int page) {

        Query multiMatchQuery = MultiMatchQuery.of(m -> m
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
        if (projectStatus != null) filters.add(projectStatusFilter);
        if (!categories.isEmpty()) filters.add(projectCategoriesFilter);
        if (!techStacks.isEmpty()) filters.add(projectTechStacksFilter);

        Query boolQuery = BoolQuery.of(b -> b
                .must(multiMatchQuery)
                .filter(filters)
        )._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<ProjectDocument> searchHits = elasticsearchOperations.search(
                nativeQuery,
                ProjectDocument.class
        );

         return searchHits.stream()
                 .map(SearchHit::getContent)
                 .map(ProjectDocument::toDocument)
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
