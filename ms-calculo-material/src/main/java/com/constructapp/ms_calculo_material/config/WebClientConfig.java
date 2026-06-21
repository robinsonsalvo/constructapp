package com.constructapp.ms_calculo_material.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${MS_CATALOGO_URL:http://localhost:8081}")
    private String catalogoUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(catalogoUrl) // consulta a catalogo
                .build();
    }
}