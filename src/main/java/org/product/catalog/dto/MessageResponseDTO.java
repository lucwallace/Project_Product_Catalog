package org.product.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Schema(description = "Mensagem retorno da Api")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDTO {

    @Schema(
            description = "Código da requisição",
            example = "200"
    )
    private int code;
    @Schema(
            description = "Mensagem da requisição",
            example = "Item criado com sucesso"
    )
    private String message;

    @Schema(
            description = "Id do processo",
            example = "1"
    )
    private Long id;

}
