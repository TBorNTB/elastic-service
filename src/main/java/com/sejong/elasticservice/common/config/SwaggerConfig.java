package com.sejong.elasticservice.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        servers = {
                @Server(url = "/elastic-service"),
                @Server(url = "/")
        },
        info = @Info(
                title = "Elastic API",
                version = "v1",
                description = "일라스틱 서비스 API 문서입니다."
        )
)
@Configuration
public class SwaggerConfig {
}