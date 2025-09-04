package com.sejong.elasticservice.document;


import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DocumentElasticDocumentRepository extends ElasticsearchRepository<DocumentDocument, String> {
}
