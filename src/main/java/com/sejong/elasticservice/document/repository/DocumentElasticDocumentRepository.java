package com.sejong.elasticservice.document.repository;


import com.sejong.elasticservice.document.domain.DocumentDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DocumentElasticDocumentRepository extends ElasticsearchRepository<DocumentDocument, String> {
}
