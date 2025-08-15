package com.sejong.elasticservice.project;


import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProjectElasticDocumentRepository extends ElasticsearchRepository<ProjectElastic,String> {
    void deleteById(Long projectId);
}
