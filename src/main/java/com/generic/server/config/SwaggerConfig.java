package com.generic.server.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
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
        .info(
            new Info()
                .title("Generic Enterprise API")
                .version("v1.0.0")
                .description(
                    "Enterprise API for core services, supporting PostgreSQL, Redis, and MongoDB.")
                .termsOfService("http://www.generic-enterprise.com/terms")
                .contact(
                    new Contact()
                        .name("Rohit Handique")
                        .url("http://www.generic-enterprise.com/support")
                        .email("rohit.handique@generic-enterprise.com"))
                .license(new License().name("Apache 2.0").url("http://springdoc.org")))
        .externalDocs(
            new ExternalDocumentation()
                .description("Generic Server Documentation")
                .url("http://www.generic-enterprise.com/docs"));
  }
}
