package com.constructapp.ms_orden_trabajo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${MS_COTIZACION_URL:http://localhost:8088}")
    private String cotizacionUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(cotizacionUrl) // consulta a ms-cotizacion
                .build();
    }
}