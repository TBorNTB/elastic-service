package com.sejong.elasticservice.domain.rag.repository;

import com.sejong.elasticservice.domain.rag.domain.SpringAiDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SpringAiDocumentRepository extends ElasticsearchRepository<SpringAiDocument, String> {

}
