package org.product.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Dados do produto")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    @Schema(
            description = "Id do produto",
            example = "1"
    )
    private Long id;
    @Schema(
            description = "Nome do produto",
            example = "Camisa"
    )
    private String name;
    @Schema(
            description = "Preço do produto",
            example = "150,00"
    )
    private Double price;
    @Schema(
            description = "URL do produto",
            example = "www.example.com.br/camisa"
    )
    private String url;
    @Schema(
            description = "Imagem do produto"
    )
    private String photo;
    @Schema(
            description = "Categoria do produto"
    )
    private CategoryDTO categoryDTO;
}
