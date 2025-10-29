package com.sejong.elasticservice.news.repository;

import com.sejong.elasticservice.news.domain.NewsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface NewsDocumentRepository extends ElasticsearchRepository<NewsDocument, String> {
}
