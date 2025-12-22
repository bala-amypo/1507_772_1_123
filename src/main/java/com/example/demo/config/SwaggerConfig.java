package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Employee Skills Matrix Search API")
                        .version("1.0")
                        .description("REST API for Employee Skills Management (Security Disabled)"))
                .servers(List.of(
                        // This matches the external URL provided by your environment
                        new Server().url("https://9380.408procr.amypo.ai/")
                ));
    }
}