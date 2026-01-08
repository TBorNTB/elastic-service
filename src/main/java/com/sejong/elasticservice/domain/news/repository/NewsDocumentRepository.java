package com.sejong.elasticservice.domain.news.repository;

import com.sejong.elasticservice.domain.news.domain.NewsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface NewsDocumentRepository extends ElasticsearchRepository<NewsDocument, String> {
}
