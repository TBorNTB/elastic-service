package com.sejong.elasticservice.csknowledge.repository;

import com.sejong.elasticservice.csknowledge.domain.CsKnowledgeDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CsKnowledgeDocumentRepository extends ElasticsearchRepository<CsKnowledgeDocument, String> {

}
