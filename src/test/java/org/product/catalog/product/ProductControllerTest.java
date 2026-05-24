package org.product.catalog.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.product.catalog.dto.CategoryDTO;
import org.product.catalog.dto.ProductDTO;
import org.product.catalog.mapper.CategoryMapper;
import org.product.catalog.model.Category;
import org.product.catalog.model.Product;
import org.product.catalog.repositories.CategoryRepository;
import org.product.catalog.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@ActiveProfiles("test")
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void deveCriarProdutoComSucesso() throws Exception {
        byte[] imageBytes = "image".getBytes();

        String base64 = Base64.getEncoder().encodeToString(imageBytes);

        Optional<Category> category = categoryRepository.findById(Long.valueOf(6));

        ProductDTO dto = new ProductDTO();
        dto.setName("Galaxy s25");
        dto.setPrice(2560D);
        dto.setUrl("https://www.teste.com/teste");
        dto.setPhoto(base64);

        if (category.isPresent()) {

            CategoryDTO categoryDTO = CategoryMapper.toDTO(category.get());

            dto.setCategoryDTO(categoryDTO);
        }

        String json = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/product/create")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.message").value("Produto criado com sucesso."));

        List<Product> produtos = productRepository.findAll();
        assertEquals(1, produtos.size());
        assertArrayEquals(imageBytes, produtos.get(0).getPhoto());

    }

    @ParameterizedTest
    @MethodSource("provideInvalidProducts")
    void deveRetornarErroQuandoCamposForemInvalidos(ProductDTO dto, int expectedStatus) throws Exception {

        String json = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/product/create")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().is(expectedStatus));
    }

    // Método gerador de dados (Source)
    private static Stream<Arguments> provideInvalidProducts() {
        CategoryDTO mockCategory = new CategoryDTO(1L, "Jóia01");
        CategoryDTO mockCategory2 = new CategoryDTO(Long.MAX_VALUE, "Jóia02");
        String base64 = "YmFzZTY0";

        return Stream.of(
                Arguments.of(new ProductDTO(null, null, 1000D, "url", base64, mockCategory), 400),
                Arguments.of(new ProductDTO(null, "Pulseira", null, "url", base64, mockCategory), 400),
                Arguments.of(new ProductDTO(null, "Pulseira", 1000D, null, base64, mockCategory), 400),
                Arguments.of(new ProductDTO(null, "Pulseira", 1000D, "url", null, mockCategory), 400),
                Arguments.of(new ProductDTO(null, "Pulseira", 1000D, "url", base64, null), 400),
                Arguments.of(new ProductDTO(Long.MAX_VALUE, "Pulseira", 1000D, "url", base64, mockCategory), 404),
                Arguments.of(new ProductDTO(null, "Pulseira", 1000D, "url", base64, mockCategory2), 404),
                Arguments.of(new ProductDTO(null, "Pulseira", 1000D, "https://www.teste.com/teste", base64, mockCategory), 201)
        );
    }

    @Test
    void deveRetornaErroProdutoDuplicado() throws Exception {

        byte[] imageBytes = "image".getBytes();

        String base64 = Base64.getEncoder().encodeToString(imageBytes);

        CategoryDTO mockCategory = new CategoryDTO(1L, "Jóia01");

        // 🔥 cria produto
        Product product = new Product();
        product.setName("Pulseira");
        product.setPrice(100.0);
        product.setCategory(CategoryMapper.toModel(mockCategory));
        product.setPhoto(imageBytes);

        product = productRepository.save(product);

        ProductDTO dto = new ProductDTO();
        dto.setName("Pulseira");
        dto.setPrice(2560D);
        dto.setUrl("https://www.teste.com/teste");
        dto.setPhoto(base64);
        dto.setCategoryDTO(mockCategory);

        String json = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/product/create")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    void deveAtualizarProduto() throws Exception {

        byte[] imageBytes = "image".getBytes();

        String base64 = Base64.getEncoder().encodeToString(imageBytes);

        CategoryDTO mockCategory = new CategoryDTO(1L, "Jóia01");

        // 🔥 cria produto
        Product product = new Product();
        product.setName("Produto Antigo");
        product.setPrice(100.0);
        product.setCategory(CategoryMapper.toModel(mockCategory));
        product.setPhoto(imageBytes);

        product = productRepository.save(product);

        // 🔥 novo DTO
        ProductDTO dto = new ProductDTO();
        dto.setName("Produto Novo");
        dto.setPrice(200.0);
        dto.setUrl("https://teste.com");
        dto.setPhoto(base64);
        dto.setCategoryDTO(mockCategory);

        String json = mapper.writeValueAsString(dto);

        // 🔥 chama update
        mockMvc.perform(put("/product/update/" + product.getId())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());

        // 🔥 valida no banco
        Product updated = productRepository.findById(product.getId()).orElseThrow();

        assertEquals("Produto Novo", updated.getName());
        assertEquals(200.0, updated.getPrice());
    }

    @Test
    void deveDeletarProduto() throws Exception {

        byte[] imageBytes = "image".getBytes();

        String base64 = Base64.getEncoder().encodeToString(imageBytes);

        CategoryDTO mockCategory = new CategoryDTO(1L, "Jóia01");

        // 🔥 cria produto
        Product product = new Product();
        product.setName("Produto Antigo");
        product.setPrice(100.0);
        product.setCategory(CategoryMapper.toModel(mockCategory));
        product.setPhoto(imageBytes);

        product = productRepository.save(product);

        // 🔥 deleta
        mockMvc.perform(delete("/product/delete/" + product.getId()))
                .andExpect(status().isOk());

        // 🔥 valida
        boolean exists = productRepository.findById(product.getId()).isPresent();

        assertFalse(exists);
    }

    @Test
    void deveListarTodosOsProdutos() throws Exception {

        byte[] imageBytes = "image".getBytes();

        String base64 = Base64.getEncoder().encodeToString(imageBytes);

        CategoryDTO mockCategory = new CategoryDTO(1L, "Jóia01");

        // 🔥 cria produto
        Product product = new Product();
        product.setName("Pulseira");
        product.setPrice(100.0);
        product.setCategory(CategoryMapper.toModel(mockCategory));
        product.setPhoto(imageBytes);

        product = productRepository.save(product);

        Product product02 = new Product();
        product02.setName("Corrente");
        product02.setPrice(120.0);
        product02.setCategory(CategoryMapper.toModel(mockCategory));
        product02.setUrl("https://teste.com");
        product02.setPhoto(imageBytes);

        product02 = productRepository.save(product02);

        mockMvc.perform(get("/product/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Pulseira"))
                .andExpect(jsonPath("$[1].name").value("Corrente"));
    }

    @Test
    void deveRetornaProdutoPorId() throws Exception {

        byte[] imageBytes = "image".getBytes();

        String base64 = Base64.getEncoder().encodeToString(imageBytes);

        CategoryDTO mockCategory = new CategoryDTO(1L, "Jóia01");

        // 🔥 cria produto
        Product product = new Product();
        product.setName("Pulseira");
        product.setPrice(100.0);
        product.setCategory(CategoryMapper.toModel(mockCategory));
        product.setPhoto(imageBytes);

        product = productRepository.save(product);

        mockMvc.perform(get("/product/findById/"+product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value("Pulseira"));
    }

    @Test
    void deveRetornarErro404AoBuscarOIdProdutoInexistente() throws Exception {

        mockMvc.perform(get("/product/findById/"+Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

}
