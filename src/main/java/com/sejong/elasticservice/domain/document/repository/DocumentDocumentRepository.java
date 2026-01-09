package com.sejong.elasticservice.domain.document.repository;


import com.sejong.elasticservice.domain.document.domain.DocumentDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DocumentDocumentRepository extends ElasticsearchRepository<DocumentDocument, String> {
}
