package com.sejong.elasticservice.domain.rag.repository;

import com.sejong.elasticservice.domain.rag.domain.RAGDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface RAGDocumentRepository extends ElasticsearchRepository<RAGDocument, String> {

}
