package com.sejong.elasticservice.domain.rag.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OpenAiConfig {

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    @Bean
    public OpenAiApi openAiApi() {
        log.info("Open API 클라이언트 초기화");
        return new OpenAiApi(apiKey);
    }
}
