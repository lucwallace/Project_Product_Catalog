package org.product.catalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.product.catalog.dto.CategoryDTO;
import org.product.catalog.dto.MessageResponseDTO;
import org.product.catalog.dto.ProductDTO;
import org.product.catalog.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/product")
@Tag(name = "Produto", description = "Operações relacionadas ao produto")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(
            summary = "Listar todos os produtos",
            description = "Endpoint responsável por retornar a lista completa de produtos cadastrados no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos retornados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Não foi possível carregar a lista de produtos. Tente novamente mais tarde.")
    })
    @GetMapping(value = "/findAll")
    private ResponseEntity<List<ProductDTO>> findAll() {
        try {

            List<ProductDTO> productDTOList = productService.findAll();

            return ResponseEntity.ok(productDTOList);

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro interno. Por favor, tente novamente mais tarde."
            );
        }
    }

    @Operation(
            summary = "Retornar o produto por id",
            description = "Endpoint responsável por retorna o produto por id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto com ID 1 não foi encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao buscar o produto. Tente novamente mais tarde.")
    })
    @GetMapping(value = "/findById/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id){
        ProductDTO productDTO = productService.findById(id);

        return ResponseEntity.ok(productDTO);
    }

    @Operation(
            summary = "Retornar o produto por categoria",
            description = "Endpoint responsável por retorna o produto por categoria."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto com a categoria Celular não foi encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao buscar o produto. Tente novamente mais tarde.")
    })
    @PostMapping(value = "/findByCategory")
    public ResponseEntity<List<ProductDTO>> findByCategory(@RequestBody CategoryDTO dto){
        List<ProductDTO> productsDTO = productService.findByCategory(dto);

        return ResponseEntity.ok(productsDTO);
    }

    @Operation(
            summary = "Listar produtos com paginação",
            description = "Endpoint responsável por retornar uma página de produtos cadastrados no sistema. " +
                    "Os parâmetros de paginação aceitos são: 'page' (número da página, começando em 0), " +
                    "'size' (quantidade de registros por página) e 'sort' (campo e direção de ordenação).",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de produtos retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao carregar categorias")
    })
    @GetMapping(value = "/list")
    private ResponseEntity<Page<ProductDTO>> listCategories(Pageable pageable) {
        try {

            Page<ProductDTO> productDTOList = productService.findList(pageable);

            return ResponseEntity.ok(productDTOList);

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro interno. Por favor, tente novamente mais tarde."
            );
        }
    }

    @Operation(
            summary = "Retornar o produto por nome",
            description = "Endpoint responsável por retorna o produto por nome.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto com nome Galaxy s25 não foi encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao buscar o produto. Tente novamente mais tarde.")
    })
    @PostMapping(value = "/findByNome")
    public ResponseEntity<ProductDTO> findByNome(@RequestBody ProductDTO dto){
        ProductDTO productDTO = productService.findByName(dto);

        return ResponseEntity.ok(productDTO);
    }

    @Operation(
            summary = "Criação do product",
            description = "Endpoint responsável por criar o produto.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "O campo 'nome da categoria' é obrigatório."),
            @ApiResponse(responseCode = "400", description = "Categoria com ID 1 não foi encontrado."),
            @ApiResponse(responseCode = "400", description = "Erro inesperado ao criar a categoria."),
            @ApiResponse(responseCode = "400", description = "O campo 'nome do produto' é obrigatório."),
            @ApiResponse(responseCode = "400", description = "O campo 'Preço' é obrigatório."),
            @ApiResponse(responseCode = "400", description = "O campo 'Foto do produto' é obrigatório."),
            @ApiResponse(responseCode = "400", description = "Já existe um produto com o nome janela."),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao criar o produto.")
    })
    @PostMapping(value = "/create")
    public ResponseEntity<MessageResponseDTO> create(@RequestBody ProductDTO productDTO) {
        MessageResponseDTO messageResponseDTO = productService.create(productDTO);

        return ResponseEntity.status(messageResponseDTO.getCode()).body(messageResponseDTO);
    }

    @Operation(
            summary = "Alteração do produto",
            description = "Endpoint responsável pela alteração do produto.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto alterado com sucesso."),
            @ApiResponse(responseCode = "400", description = "O campo 'nome da categoria' é obrigatório."),
            @ApiResponse(responseCode = "400", description = "Categoria com ID 1 não foi encontrado."),
            @ApiResponse(responseCode = "400", description = "Erro inesperado ao criar a categoria."),
            @ApiResponse(responseCode = "400", description = "O campo 'nome do produto' é obrigatório."),
            @ApiResponse(responseCode = "400", description = "O campo 'Preço' é obrigatório."),
            @ApiResponse(responseCode = "400", description = "O campo 'Foto do produto' é obrigatório."),
            @ApiResponse(responseCode = "400", description = "Já existe um produto com o nome janela."),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao alterar o produto.")
    })
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<MessageResponseDTO> update(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        productDTO.setId(id);
        MessageResponseDTO messageResponseDTO = productService.update(productDTO);

        return ResponseEntity.ok(messageResponseDTO);
    }

    @Operation(
            summary = "Exclusão do produto",
            description = "Endpoint responsável pela exclusão do produto.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto deletado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Produto com ID 1 não foi encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao deletar o produto.")
    })
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<MessageResponseDTO> delete(@PathVariable Long id){
        MessageResponseDTO messageResponseDTO = productService.delete(id);

        return ResponseEntity.ok(messageResponseDTO);
    }

}
