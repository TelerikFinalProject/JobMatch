package com.telerikacademy.web.jobmatch.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "API Documentation", version = "1.0"),
        security = @SecurityRequirement(name = "bearerAuth") // Ensure this matches the name used in the security scheme
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", // SecurityScheme name
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP) // Authentication type
                                        .scheme("bearer") // Bearer scheme for token
                                        .bearerFormat("JWT") // Optional, specifies that it's a JWT token
                        ));
    }
}
