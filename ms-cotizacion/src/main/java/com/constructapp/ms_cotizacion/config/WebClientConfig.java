package com.constructapp.ms_cotizacion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${MS_CLIENTE_URL:http://localhost:8082}")
    private String clienteUrl;

    @Value("${MS_PROVEEDOR_MATERIAL_URL:http://localhost:8083}")
    private String proveedorMaterialUrl;

    @Value("${MS_PROVEEDOR_SERVICIO_URL:http://localhost:8084}")
    private String proveedorServicioUrl;

    @Bean(name = "webClientCliente")
    public WebClient webClientCliente() {
        return WebClient.builder()
                .baseUrl(clienteUrl) // consulta a ms-cliente
                .build();
    }

    @Bean(name = "webClientProveedorMaterial")
    public WebClient webClientProveedorMaterial() {
        return WebClient.builder()
                .baseUrl(proveedorMaterialUrl) // consulta a ms-proveedor-material
                .build();
    }

    @Bean(name = "webClientProveedorServicio")
    public WebClient webClientProveedorServicio() {
        return WebClient.builder()
                .baseUrl(proveedorServicioUrl) // consulta a ms-proveedor-servicio
                .build();
    }
}