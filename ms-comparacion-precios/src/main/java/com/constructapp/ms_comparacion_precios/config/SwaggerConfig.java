package com.constructapp.ms_comparacion_precios.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS Comparación de Precios API")
                        .description("Comparación de precios de materiales entre proveedores")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ConstructApp Team")
                                .email("constructapp@gmail.com"))
                        .license(new License()
                                .name("MIT License")));
    }
}
