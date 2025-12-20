package com.example.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "ACS PR3 — Authors & Books REST API",
                version = "1.0",
                description = "REST API с поддержкой JSON/XML + XSL для отображения XML в браузере."
        )
)
public class OpenApiConfig {
}
