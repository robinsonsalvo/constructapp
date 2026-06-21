package com.constructapp.ms_orden_trabajo.config;

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
                        .title("MS Orden de Trabajo API")
                        .description("Gestión de órdenes de trabajo y seguimiento de estados")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ConstructApp Team")
                                .email("constructapp@gmail.com"))
                        .license(new License()
                                .name("MIT License")));
    }
}
