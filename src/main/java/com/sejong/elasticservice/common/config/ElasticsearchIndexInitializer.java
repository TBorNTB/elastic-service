java
    package com.sejong.elasticservice.common.config;

import com.sejong.elasticservice.domain.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.domain.document.domain.DocumentDocument;
import com.sejong.elasticservice.domain.news.domain.NewsDocument;
import com.sejong.elasticservice.domain.project.domain.ProjectDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;

@Configuration
public class ElasticsearchIndexInitializer {

    @Value("${elasticsearch.index.recreate:false}")
    private boolean recreate;

    @Bean
    public ApplicationRunner createCsKnowledgeIndex(ElasticsearchOperations ops) {
        return args -> initIndex(ops, CsKnowledgeDocument.class);
    }

    @Bean
    public ApplicationRunner createProjectIndex(ElasticsearchOperations ops) {
        return args -> initIndex(ops, ProjectDocument.class);
    }

    @Bean
    public ApplicationRunner createNewsIndex(ElasticsearchOperations ops) {
        return args -> initIndex(ops, NewsDocument.class);
    }

    @Bean
    public ApplicationRunner createDocumentIndex(ElasticsearchOperations ops) {
        return args -> initIndex(ops, DocumentDocument.class);
    }

    private void initIndex(ElasticsearchOperations ops, Class<?> clazz) {
        IndexOperations indexOps = ops.indexOps(clazz);

        if (!indexOps.exists()) {
            indexOps.createWithMapping();
            return;
        }

        if (recreate) {
            indexOps.delete();
            indexOps.createWithMapping();
        }
    }
}