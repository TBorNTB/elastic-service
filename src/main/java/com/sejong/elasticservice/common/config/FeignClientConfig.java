package com.sejong.elasticservice.common.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.sejong.elasticservice.client")
public class FeignClientConfig {
}
