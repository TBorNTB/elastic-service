package com.sejong.elasticservice.document.repository;


import com.sejong.elasticservice.document.domain.DocumentDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DocumentDocumentRepository extends ElasticsearchRepository<DocumentDocument, String> {
}
