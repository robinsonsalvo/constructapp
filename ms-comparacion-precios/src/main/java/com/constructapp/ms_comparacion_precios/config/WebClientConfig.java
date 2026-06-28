package com.constructapp.ms_comparacion_precios.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

<<<<<<< HEAD
    @Value("${MS_PROVEEDOR_MATERIAL_URL:http://localhost:8083}")
=======
    @Value("${MS_PROVEEDOR_MATERIAL_URL:http://ms-proveedor-material:8083}")
>>>>>>> 16dc53c (fix de eureka, pruebas unitarias y swagger auth)
    private String proveedorMaterialUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
<<<<<<< HEAD
                .baseUrl(proveedorMaterialUrl) // consulta a ms-proveedor-material
=======
                .baseUrl(proveedorMaterialUrl)
>>>>>>> 16dc53c (fix de eureka, pruebas unitarias y swagger auth)
                .build();
    }
}