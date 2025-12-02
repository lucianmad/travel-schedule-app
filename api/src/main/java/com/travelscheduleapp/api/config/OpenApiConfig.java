package com.travelscheduleapp.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI travelScheduleOpenApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Travel Schedule API")
                                .description("API for generating and managing travel itineraries")
                                .version("1.0")
                )
                .addSecurityItem(new SecurityRequirement().addList("UserIdAuth"))
                .components(new Components()
                        .addSecuritySchemes("UserIdAuth", new SecurityScheme()
                                .name("X-User-Id")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                        )
                );
    }
}
