package com.br.eventmanagement.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
public class OpenApi {

    @Bean
    public OpenAPI openAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Development");

        return new OpenAPI()
                .info(new Info()
                        .title("Event Management")
                        .version("v1.0.0")
                        .description("Application to manage events")
                        .termsOfService("https://github.com/AllisonMarquesSouza/EventManagement")
                        .contact(new Contact()
                                .name("Support for email")
                                .email("allisonsouza10261@gmail.com"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("Event Management GitHub Documentation")
                        .url("https://github.com/AllisonMarquesSouza/EventManagement")
                )
                .servers(List.of(server))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                ).security(Collections.singletonList(new SecurityRequirement().addList("bearerAuth")));

    }
}