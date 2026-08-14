package com.alvaropaiva.SistemaDePedidos.resources;

import com.alvaropaiva.SistemaDePedidos.entities.Category;
import com.alvaropaiva.SistemaDePedidos.services.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes da camada web de CategoryResource (somente leitura).
 */
@WebMvcTest(CategoryResource.class)
@ActiveProfiles("test")
class CategoryResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService service;

    @Test
    @DisplayName("GET /categories devolve 200 e a lista de categorias")
    void findAllDevolve200() throws Exception {
        when(service.findALl()).thenReturn(List.of(
                new Category(1L, "Electronics"),
                new Category(2L, "Books")
        ));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[1].name").value("Books"));
    }

    @Test
    @DisplayName("GET /categories/{id} devolve a categoria")
    void findByIdDevolveACategoria() throws Exception {
        when(service.findById(2L)).thenReturn(new Category(2L, "Books"));

        mockMvc.perform(get("/categories/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Books"));
    }

    @Test
    @DisplayName("a categoria nao serializa seus produtos (evita recursao infinita)")
    void categoriaNaoSerializaOsProdutos() throws Exception {
        when(service.findById(2L)).thenReturn(new Category(2L, "Books"));

        mockMvc.perform(get("/categories/{id}", 2L))
                .andExpect(jsonPath("$.products").doesNotExist());
    }
}
