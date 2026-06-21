package com.constructapp.ms_proyecto.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${MS_CLIENTE_URL:http://localhost:8082}")
    private String clienteUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(clienteUrl) // consulta a ms-cliente
                .build();
    }
}
