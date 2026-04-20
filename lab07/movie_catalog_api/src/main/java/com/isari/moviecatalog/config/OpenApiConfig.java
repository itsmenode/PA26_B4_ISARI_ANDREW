package com.isari.moviecatalog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI movieCatalogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Movie Catalog API")
                        .description("REST API for managing movies, genres, actors, and reports (PA Lab 07)")
                        .version("1.0.0")
                        .contact(new Contact().name("Isari Andrew"))
                        .license(new License().name("Academic use")));
    }
}