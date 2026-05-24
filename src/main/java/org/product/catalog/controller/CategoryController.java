package org.product.catalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.product.catalog.dto.CategoryDTO;
import org.product.catalog.dto.MessageResponseDTO;
import org.product.catalog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/category")
@Tag(name = "Categoria", description = "Operações relacionadas a categoria")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(
            summary = "Listar todas as categorias",
            description = "Endpoint responsável por retornar a lista completa de categorias cadastradas no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categorias retornadas com sucesso"),
            @ApiResponse(responseCode = "500", description = "Não foi possível carregar a lista de categorias. Tente novamente mais tarde.")
    })
    @GetMapping(value = "/findAll")
    private ResponseEntity<List<CategoryDTO>> findAll() {
        try {

            List<CategoryDTO> categoryDTOList = categoryService.findAll();

            return ResponseEntity.ok(categoryDTOList);

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro interno. Por favor, tente novamente mais tarde."
            );
        }
    }

    @Operation(
            summary = "Listar categorias com paginação",
            description = "Endpoint responsável por retornar uma página de categorias cadastradas no sistema. " +
                    "Os parâmetros de paginação aceitos são: 'page' (número da página, começando em 0), " +
                    "'size' (quantidade de registros por página) e 'sort' (campo e direção de ordenação)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de categorias retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao carregar categorias")
    })
    @GetMapping(value = "/list")
    private ResponseEntity<Page<CategoryDTO>> listCategories(Pageable pageable) {
        try {

            Page<CategoryDTO> categoryDTOList = categoryService.findList(pageable);

            return ResponseEntity.ok(categoryDTOList);

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro interno. Por favor, tente novamente mais tarde."
            );
        }
    }

    @Operation(
            summary = "Retornar a categoria por id",
            description = "Endpoint responsável por retorna a categoria por id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categorias retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria com ID 1 não foi encontrada."),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao buscar a categoria. Tente novamente mais tarde.")
    })
    @GetMapping(value = "/findById/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable Long id){
        CategoryDTO categoryDTO = categoryService.findById(id);

        return ResponseEntity.ok(categoryDTO);
    }

    @Operation(
            summary = "Retornar a categoria por nome",
            description = "Endpoint responsável por retorna a categoria por nome."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categorias retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria com nome Ar não foi encontrada."),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao buscar a categoria. Tente novamente mais tarde.")
    })
    @PostMapping(value = "/findByNome")
    public ResponseEntity<CategoryDTO> findByNome(@RequestBody CategoryDTO dto){
        CategoryDTO categoryDTO = categoryService.findByNome(dto);

        return ResponseEntity.ok(categoryDTO);
    }

    @Operation(
            summary = "Criação da categoria",
            description = "Endpoint responsável por criar a categoria.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso."),
            @ApiResponse(responseCode = "400", description = "O campo 'nome da categoria' é obrigatório."),
            @ApiResponse(responseCode = "400", description = "Já existe uma categoria com o nome leitura."),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao criar a categoria.")
    })
    @PostMapping(value = "/create")
    public ResponseEntity<MessageResponseDTO> create(@RequestBody CategoryDTO categoryDTO) {
        MessageResponseDTO messageResponseDTO = categoryService.create(categoryDTO);

        return ResponseEntity.status(messageResponseDTO.getCode()).body(messageResponseDTO);
    }

    @Operation(
            summary = "Alteração da categoria",
            description = "Endpoint responsável pela alteração da categoria.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria alterada com sucesso."),
            @ApiResponse(responseCode = "400", description = "O campo 'nome da categoria' é obrigatório."),
            @ApiResponse(responseCode = "400", description = "Já existe uma categoria com o nome leitura."),
            @ApiResponse(responseCode = "404", description = "Categoria com ID 1 não foi encontrada."),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao alterar a categoria.")
    })
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<MessageResponseDTO> update(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        categoryDTO.setId(id);
        MessageResponseDTO messageResponseDTO = categoryService.update(categoryDTO);

        return ResponseEntity.status(messageResponseDTO.getCode()).body(messageResponseDTO);
    }

    @Operation(
            summary = "Exclusão da categoria",
            description = "Endpoint responsável pela exclusão da categoria.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria deletada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Categoria com ID 1 não foi encontrada."),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao deletar a categoria.")
    })
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<MessageResponseDTO> delete(@PathVariable Long id){
        MessageResponseDTO messageResponseDTO = categoryService.delete(id);

        return ResponseEntity.status(messageResponseDTO.getCode()).body(messageResponseDTO);
    }

}
