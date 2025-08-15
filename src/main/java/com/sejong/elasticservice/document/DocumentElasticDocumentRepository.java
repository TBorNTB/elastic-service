package com.sejong.projectservice.infrastructure.document.repository;

import com.sejong.projectservice.infrastructure.document.entity.DocumentElastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DocumentElasticDocumentRepository extends ElasticsearchRepository<DocumentElastic, String> {
}
