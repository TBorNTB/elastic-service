package com.sejong.elasticservice.domain.project.repository;


import com.sejong.elasticservice.domain.project.domain.ProjectDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProjectElasticDocumentRepository extends ElasticsearchRepository<ProjectDocument,String> {
    void deleteById(Long projectId);
}
