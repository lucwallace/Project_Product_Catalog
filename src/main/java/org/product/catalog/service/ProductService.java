package org.product.catalog.service;

import org.product.catalog.dto.CategoryDTO;
import org.product.catalog.dto.MessageResponseDTO;
import org.product.catalog.dto.ProductDTO;
import org.product.catalog.mapper.ProductMapper;
import org.product.catalog.model.Product;
import org.product.catalog.repositories.CategoryRepository;
import org.product.catalog.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<ProductDTO> findAll() {
        try {
            List<Product> products = productRepository.findAll();

            return products.stream()
                    .map(ProductMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Não foi possível carregar a lista de produtos. Tente novamente mais tarde."
            );
        }
    }

    public ProductDTO findById(Long id) {
        try {
            Optional<Product> obj = productRepository.findById(id);

            if (obj.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Produto com ID %d não foi encontrada.", id)
                );
            }

            return ProductMapper.toDTO(obj.get());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro inesperado ao buscar o produto. Tente novamente mais tarde."
            );
        }
    }

    public ProductDTO findByName(ProductDTO productDTO) {
        try {
            Optional<Product> obj = productRepository.findByName(productDTO.getName());

            if (obj.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Produto com nome %s não foi encontrado.", productDTO.getName())
                );
            }

            return ProductMapper.toDTO(obj.get());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro inesperado ao buscar o produto. Tente novamente mais tarde."
            );
        }
    }

    public List<ProductDTO> findByCategory(CategoryDTO categoryDTO) {
        try {
            List<Product> products = productRepository.findByCategoryName(categoryDTO.getName());

            if (products.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Produto com a categoria %s não foi encontrado.", categoryDTO.getName())
                );
            }

            return products.stream()
                    .map(ProductMapper::toDTO)
                    .collect(Collectors.toList());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro inesperado ao buscar o produto. Tente novamente mais tarde."
            );
        }
    }

    public Page<ProductDTO> findList(Pageable pageable) {
        try {
            Page<Product> products = productRepository.findAll(pageable);

            return products.map(ProductMapper::toDTO);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Não foi possível carregar a lista de produtos. Tente novamente mais tarde."
            );
        }
    }

    public MessageResponseDTO create(ProductDTO productDTO) {
        try {
            validate(productDTO);

            Product product = ProductMapper.toModel(productDTO);
            product.setDateCreate(Timestamp.from(Instant.now()));

           product = productRepository.save(product);

            return new MessageResponseDTO(HttpStatus.CREATED.value(), "Produto criado com sucesso.", product.getId());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro inesperado ao criar o produto."
            );
        }
    }

    public MessageResponseDTO update(ProductDTO productDTO) {
        try {
            validate(productDTO);

            Product product = ProductMapper.toModel(productDTO);
            product.setDateUpdate(Timestamp.from(Instant.now()));

            product = productRepository.save(product);

            return new MessageResponseDTO(HttpStatus.OK.value(), "Produto alterado com sucesso.", product.getId());

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro inesperado ao alterar o produto."
            );
        }
    }

    public MessageResponseDTO delete(Long id) {
        try {
            if (!productRepository.existsById(id)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Porduto com ID %d não foi encontrado.", id)
                );
            }

            productRepository.deleteById(id);

            return new MessageResponseDTO(HttpStatus.OK.value(), "Produto deletado com sucesso.", id);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro inesperado ao deletar o produto."
            );
        }
    }

    public void validate(ProductDTO productDTO) {

        if (productDTO.getId() != null && !productRepository.existsById(productDTO.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("Produto com ID %d não foi encontrado.", productDTO.getId())
            );
        }

        if (productDTO.getCategoryDTO() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O campo 'nome da categoria' é obrigatório."
            );
        }

        if (!categoryRepository.existsById(productDTO.getCategoryDTO().getId())) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Categoria com ID %d não foi encontrado.", productDTO.getCategoryDTO().getId())
                );
        }

        if (productDTO.getName() == null || productDTO.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O campo 'nome do produto' é obrigatório."
            );
        }

        if (productDTO.getPrice() == null || productDTO.getPrice().compareTo(0.0) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O campo 'Preço' é obrigatório."
            );
        }

        if (productDTO.getPhoto() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O campo 'Foto do produto' é obrigatório."
            );
        }

        if (productDTO.getUrl() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O campo 'URL' é obrigatório."
            );
        }

        if (!isValidUrl(productDTO.getUrl())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Formato de URL inválido."
            );
        }

        Optional<Product> existing = productRepository.findByName(productDTO.getName());
        if (existing.isPresent() && !existing.get().getId().equals(productDTO.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    String.format("Já existe um produto com o nome '%s'.", productDTO.getName())
            );
        }

    }

    public static boolean isValidUrl(String url) {

        if (url == null || url.isBlank()) {
            return false;
        }

        // 🔥 REGEX (validação inicial)
        String regex = "^(https?://)([\\w-]+\\.)+[\\w-]+(:\\d+)?(/[^\\s]*)?$";

        if (!url.matches(regex)) {
            return false;
        }

        try {
            URI uri = new URI(url.trim());

            if (uri.getScheme() == null ||
                    (!uri.getScheme().equalsIgnoreCase("http") &&
                            !uri.getScheme().equalsIgnoreCase("https"))) {
                return false;
            }

            if (uri.getAuthority() == null) {
                return false;
            }

            uri.toURL();

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
