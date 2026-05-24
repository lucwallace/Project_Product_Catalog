package org.product.catalog.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.product.catalog.dto.CategoryDTO;
import org.product.catalog.dto.MessageResponseDTO;
import org.product.catalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@ActiveProfiles("test")
class CategoryControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void cleanDatabase() {
        categoryRepository.deleteAll();
    }

    @Test
    void deveCriarCategoriaComSucesso() throws Exception {

        CategoryDTO dto = new CategoryDTO();
        dto.setName("Móveis");

        String json = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/category/create")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.message").value("Categoria criada com sucesso."));
    }

    @Test
    void deveRetornarErro400QuandoCategoriaSemNomeCriar() throws Exception {

        CategoryDTO dto = new CategoryDTO();

        String json = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/category/create")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarErro400QuandoCategoriaDuplicadaCriar() throws Exception {

        CategoryDTO dto1 = new CategoryDTO();
        dto1.setName("Decoração");

        String json1 = mapper.writeValueAsString(dto1);

        mockMvc.perform(post("/category/create")
                        .contentType("application/json")
                        .content(json1))
                .andExpect(status().isCreated());

        CategoryDTO dto2 = new CategoryDTO();
        dto2.setName("Decoração");

        String json2 = mapper.writeValueAsString(dto2);

        mockMvc.perform(post("/category/create")
                        .contentType("application/json")
                        .content(json2))
                .andExpect(status().isConflict());
    }

    @Test
    void deveListarTodasAsCategorias() throws Exception {

        CategoryDTO dto1 = new CategoryDTO();
        dto1.setName("Ferramentas");

        String json1 = mapper.writeValueAsString(dto1);

        mockMvc.perform(post("/category/create")
                        .contentType("application/json")
                        .content(json1))
                .andExpect(status().isCreated());

        CategoryDTO dto2 = new CategoryDTO();
        dto2.setName("Moda");

        String json2 = mapper.writeValueAsString(dto2);

        mockMvc.perform(post("/category/create")
                        .contentType("application/json")
                        .content(json2))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/category/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Ferramentas"))
                .andExpect(jsonPath("$[1].name").value("Moda"));;
    }

    @Test
    void deveRetornar0AoListarTodasAsCategorias() throws Exception {

        mockMvc.perform(get("/category/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deveRetornarCategoriaPorId() throws Exception {

        CategoryDTO dto1 = new CategoryDTO();
        dto1.setName("Ar");

        String json1 = mapper.writeValueAsString(dto1);

        ResultActions result = mockMvc.perform(post("/category/create")
                        .contentType("application/json")
                        .content(json1))
                .andExpect(status().isCreated());

        String returnJson = result.andReturn().getResponse().getContentAsString();

        MessageResponseDTO created = mapper.readValue(returnJson, MessageResponseDTO.class);

        mockMvc.perform(get("/category/findById/"+created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.name").value("Ar"));
    }

    @Test
    void deveRetornarErro404AoBuscarOIdCategoriaInexistente() throws Exception {

        mockMvc.perform(get("/category/findById/"+Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarCategoriaPorNome() throws Exception {

        CategoryDTO dto1 = new CategoryDTO();
        dto1.setName("Informática");

        String json1 = mapper.writeValueAsString(dto1);

        ResultActions result = mockMvc.perform(post("/category/create")
                        .contentType("application/json")
                        .content(json1))
                .andExpect(status().isCreated());

        String returnJson = result.andReturn().getResponse().getContentAsString();

        MessageResponseDTO created = mapper.readValue(returnJson, MessageResponseDTO.class);

        mockMvc.perform(post("/category/findByNome")
                .contentType("application/json")
                .content(json1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.name").value("Informática"));
    }

    @Test
    void deveRetornarErro404AoBuscarONomeCategoriaInexistente() throws Exception {

        CategoryDTO dto1 = new CategoryDTO();
        dto1.setName("Casa e construção");

        String json1 = mapper.writeValueAsString(dto1);

        mockMvc.perform(post("/category/findByNome")
                .contentType("application/json")
                .content(json1))
                .andExpect(status().isNotFound());
    }

    private void criarCategoria(String nome) throws Exception {

        CategoryDTO dto = new CategoryDTO();
        dto.setName(nome);

        String json = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/category/create")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRetornarPrimeiraPaginaComCincoElementos() throws Exception {

        List<String> nomes = List.of("Games", "Tvs", "Áudio", "Relógios", "Jóias", "DVDs");
        for (String nome : nomes) {
            criarCategoria(nome);
        }

        mockMvc.perform(get("/category/list?page=0&size=5&sort=name,asc")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(5))
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void deveRetornarSegundaPaginaComUmElemento() throws Exception {

        List<String> nomes = List.of("Games", "Tvs", "Áudio", "Relógios", "Jóias");
        for (String nome : nomes) {
            criarCategoria(nome);
        }

        mockMvc.perform(get("/category/list?page=0&size=5&sort=name,asc")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.content[4].name").value("Áudio"))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void deveAlterarCategoriaComSucesso() throws Exception {

        CategoryDTO dto1 = new CategoryDTO();
        dto1.setName("Informática");

        String json1 = mapper.writeValueAsString(dto1);

        ResultActions resultCreated = mockMvc.perform(post("/category/create")
                        .contentType("application/json")
                        .content(json1))
                .andExpect(status().isCreated());

        String returnJsonCreated = resultCreated.andReturn().getResponse().getContentAsString();

        MessageResponseDTO created = mapper.readValue(returnJsonCreated, MessageResponseDTO.class);

        CategoryDTO dto2 = new CategoryDTO();
        dto2.setName("Eletrônico");

        String json2 = mapper.writeValueAsString(dto2);

        ResultActions resultUpdate = mockMvc.perform(put("/category/update/"+created.getId())
                        .contentType("application/json")
                        .content(json2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.message").value("Categoria alterada com sucesso."));

        String returnJsonUpdate = resultUpdate.andReturn().getResponse().getContentAsString();

        MessageResponseDTO update = mapper.readValue(returnJsonUpdate, MessageResponseDTO.class);

        mockMvc.perform(get("/category/findById/"+update.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(update.getId()))
                .andExpect(jsonPath("$.name").value("Eletrônico"));
    }

    @Test
    void deveRetornarErro404AoAlterarONomeCategoriaInexistente() throws Exception {

        CategoryDTO dto1 = new CategoryDTO();
        dto1.setName("Casa e construção");

        String json1 = mapper.writeValueAsString(dto1);

        mockMvc.perform(put("/category/update/1")
                        .contentType("application/json")
                        .content(json1))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveExcluirCategoriaComSucesso() throws Exception {

        CategoryDTO dto1 = new CategoryDTO();
        dto1.setName("Informática");

        String json1 = mapper.writeValueAsString(dto1);

        ResultActions resultCreated = mockMvc.perform(post("/category/create")
                        .contentType("application/json")
                        .content(json1))
                .andExpect(status().isCreated());

        String returnJsonCreated = resultCreated.andReturn().getResponse().getContentAsString();

        MessageResponseDTO created = mapper.readValue(returnJsonCreated, MessageResponseDTO.class);

        ResultActions resultDelete = mockMvc.perform(delete("/category/delete/"+created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Categoria deletada com sucesso."));

        String returnJsonDelete = resultDelete.andReturn().getResponse().getContentAsString();

        MessageResponseDTO delete = mapper.readValue(returnJsonDelete, MessageResponseDTO.class);

        mockMvc.perform(get("/category/findById/"+delete.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarErro404AoDeletarACategoriaInexistente() throws Exception {

        mockMvc.perform(delete("/category/delete/1"))
                .andExpect(status().isNotFound());
    }

}