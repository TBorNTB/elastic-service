package com.sejong.elasticservice.common.config;

import com.sejong.elasticservice.domain.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.domain.document.domain.DocumentDocument;
import com.sejong.elasticservice.domain.news.domain.NewsDocument;
import com.sejong.elasticservice.domain.project.domain.ProjectDocument;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;

@Configuration
public class ElasticsearchIndexInitializer {

    @Bean
    public ApplicationRunner createCsKnowledgeIndex(ElasticsearchOperations ops) {
        return args -> {
            IndexOperations indexOps = ops.indexOps(CsKnowledgeDocument.class);
            if(indexOps.exists()) {
                indexOps.delete();
            }
            indexOps.createWithMapping();
        };
    }
    @Bean
    public ApplicationRunner createProjectIndex(ElasticsearchOperations ops) {
        return args -> {
            IndexOperations indexOps = ops.indexOps(ProjectDocument.class);
            if(indexOps.exists()) {
                indexOps.delete();
            }
            indexOps.createWithMapping();
        };
    }
    @Bean
    public ApplicationRunner createNewsIndex(ElasticsearchOperations ops) {
        return args -> {
            IndexOperations indexOps = ops.indexOps(NewsDocument.class);
            if(indexOps.exists()) {
                indexOps.delete();
            }
            indexOps.createWithMapping();
        };
    }
    @Bean
    public ApplicationRunner createDocumentIndex(ElasticsearchOperations ops) {
        return args -> {
            IndexOperations indexOps = ops.indexOps(DocumentDocument.class);
            if(indexOps.exists()) {
                indexOps.delete();
            }
            indexOps.createWithMapping();
        };
    }
}
