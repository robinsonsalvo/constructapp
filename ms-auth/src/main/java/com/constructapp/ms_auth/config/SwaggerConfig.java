package com.constructapp.ms_auth.config;

import io.swagger.v3.oas.models.OpenAPI;
<<<<<<< HEAD
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
=======
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

>>>>>>> 16dc53c (fix de eureka, pruebas unitarias y swagger auth)
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
<<<<<<< HEAD
        return new OpenAPI()
=======

        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()

>>>>>>> 16dc53c (fix de eureka, pruebas unitarias y swagger auth)
                .info(new Info()
                        .title("MS Auth API")
                        .description("Autenticación y autorización con JWT")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ConstructApp Team")
                                .email("constructapp@gmail.com"))
                        .license(new License()
<<<<<<< HEAD
                                .name("MIT License")));
    }
}
=======
                                .name("MIT License")))

                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(securitySchemeName)
                )

                .components(
                        new Components()
                                .addSecuritySchemes(
                                        securitySchemeName,
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }
}
>>>>>>> 16dc53c (fix de eureka, pruebas unitarias y swagger auth)
