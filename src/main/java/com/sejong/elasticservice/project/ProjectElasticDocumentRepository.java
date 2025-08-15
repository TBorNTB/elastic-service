package com.sejong.projectservice.infrastructure.project.repository;

import com.sejong.projectservice.infrastructure.project.entity.ProjectElastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProjectElasticDocumentRepository extends ElasticsearchRepository<ProjectElastic,String> {
    void deleteById(Long projectId);
}
