package org.product.catalog.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfiguration {
    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Servidor Local")
                ))
                .info(new Info()
                        .title("API - Catálago de produtos")
                        .version("1.0.0")
                        .description("API para gerenciamento de dados"))
                .tags(List.of(
                        new Tag().name("Categoria").description("Processo da categoria"),
                        new Tag().name("Produto").description("Processo do produto")
                ));
    }
}
