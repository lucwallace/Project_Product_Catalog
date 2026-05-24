package org.product.catalog.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.product.catalog.dto.CategoryDTO;
import org.product.catalog.dto.MessageResponseDTO;
import org.product.catalog.mapper.CategoryMapper;
import org.product.catalog.model.Category;
import org.product.catalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private static final Logger logger = LogManager.getLogger(CategoryService.class);

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryDTO> findAll() {
        try {
            List<Category> categories = categoryRepository.findAll();

            return categories.stream()
                    .map(CategoryMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Não foi possível carregar a lista de categorias. Tente novamente mais tarde."
            );
        }
    }

    public Page<CategoryDTO> findList(Pageable pageable) {
        try {
            Page<Category> categories = categoryRepository.findAll(pageable);

            return categories.map(CategoryMapper::toDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Não foi possível carregar a lista de categorias. Tente novamente mais tarde."
            );
        }
    }

    public CategoryDTO findById(Long id) {
        try {
            Optional<Category> obj = categoryRepository.findById(id);

            if (obj.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Categoria com ID %d não foi encontrada.", id)
                );
            }

            return CategoryMapper.toDTO(obj.get());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro inesperado ao buscar a categoria. Tente novamente mais tarde."
            );
        }
    }

    public CategoryDTO findByNome(CategoryDTO categoryDTO) {
        try {
            Optional<Category> obj = categoryRepository.findByName(categoryDTO.getName());

            if (obj.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Categoria com nome %s não foi encontrado.", categoryDTO.getName())
                );
            }

            return CategoryMapper.toDTO(obj.get());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro inesperado ao buscar a categoria. Tente novamente mais tarde."
            );
        }
    }

    public MessageResponseDTO create(CategoryDTO categoryDTO) {
        try {
            logger.info("Create Category");
            validate(categoryDTO);

            Category category = CategoryMapper.toModel(categoryDTO);
            category.setDateCreate(Timestamp.from(Instant.now()));

            category = categoryRepository.save(category);

            return new MessageResponseDTO(HttpStatus.CREATED.value(), "Categoria criada com sucesso.", category.getId());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Erro ao criar o categoria: " + e.getMessage());
            logger.error("Erro no create: {}, {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro inesperado ao criar a categoria."
            );
        }
    }

    public MessageResponseDTO update(CategoryDTO categoryDTO) {
        try {
            validate(categoryDTO);

            Category category = CategoryMapper.toModel(categoryDTO);
            category.setDateUpdate(Timestamp.from(Instant.now()));

            category = categoryRepository.save(category);

            return new MessageResponseDTO(HttpStatus.OK.value(), "Categoria alterada com sucesso.", category.getId());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro inesperado ao alterar a categoria."
            );
        }
    }

    public MessageResponseDTO delete(Long id) {
        try {
            if (!categoryRepository.existsById(id)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Categoria com ID %d não foi encontrada.", id)
                );
            }

            categoryRepository.deleteById(id);

            return new MessageResponseDTO(HttpStatus.OK.value(), "Categoria deletada com sucesso.", id);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro inesperado ao deletar a categoria."
            );
        }
    }

    public void validate(CategoryDTO categoryDTO) {

        if (categoryDTO.getId() != null && !categoryRepository.existsById(categoryDTO.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Categoria com ID %d não foi encontrada.", categoryDTO.getId())
                );
        }

        if (categoryDTO.getName() == null || categoryDTO.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O campo 'nome da categoria' é obrigatório."
            );
        }

        Optional<Category> existing = categoryRepository.findByName(categoryDTO.getName());
        if (existing.isPresent() && !existing.get().getId().equals(categoryDTO.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    String.format("Já existe uma categoria com o nome '%s'.", categoryDTO.getName())
            );
        }

    }

}
