package com.constructapp.ms_comparacion_precios.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${MS_PROVEEDOR_MATERIAL_URL:http://localhost:8083}")
    private String proveedorMaterialUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(proveedorMaterialUrl) // consulta a ms-proveedor-material
                .build();
    }
}