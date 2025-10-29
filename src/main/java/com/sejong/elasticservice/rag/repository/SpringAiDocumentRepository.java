package com.sejong.elasticservice.rag.repository;

import com.sejong.elasticservice.rag.domain.SpringAiDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SpringAiDocumentRepository extends ElasticsearchRepository<SpringAiDocument, String> {

}
