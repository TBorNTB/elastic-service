package com.sejong.elasticservice.domain.csknowledge.repository;

import com.sejong.elasticservice.domain.csknowledge.domain.CsKnowledgeDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CsKnowledgeDocumentRepository extends ElasticsearchRepository<CsKnowledgeDocument, String> {

}
