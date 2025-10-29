package com.sejong.elasticservice.rag.service;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

    private final OpenAiApi openAiApi;

    @Value("${spring.ai.openai.embedding.options.model}")
    private String embeddingModelName;

    private volatile OpenAiEmbeddingModel embeddingModel;

    public EmbeddingService(OpenAiApi openAiApi) {
        this.openAiApi = openAiApi;
    }

    /** OpenAI 임베딩 모델 인스턴스 (lazy init) */
    public OpenAiEmbeddingModel getEmbeddingModel() {
        if (embeddingModel == null) {
            synchronized (this) {
                if (embeddingModel == null) {
                    embeddingModel = new OpenAiEmbeddingModel(
                            openAiApi,
                            MetadataMode.EMBED,
                            OpenAiEmbeddingOptions.builder()
                                    .model(embeddingModelName)
                                    .build(),
                            RetryUtils.DEFAULT_RETRY_TEMPLATE
                    );
                }
            }
        }
        return embeddingModel;
    }
}
