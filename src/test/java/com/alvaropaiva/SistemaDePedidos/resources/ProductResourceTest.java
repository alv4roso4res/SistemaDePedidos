package com.alvaropaiva.SistemaDePedidos.resources;

import com.alvaropaiva.SistemaDePedidos.entities.Category;
import com.alvaropaiva.SistemaDePedidos.entities.Product;
import com.alvaropaiva.SistemaDePedidos.services.ProductService;
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
 * Testes da camada web de ProductResource (somente leitura).
 */
@WebMvcTest(ProductResource.class)
@ActiveProfiles("test")
class ProductResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService service;

    private Product livro() {
        Product p = new Product(null, 90.5, "Lorem ipsum dolor sit amet.", "The Lord of the Rings");
        p.setId(1L);
        p.getCategories().add(new Category(2L, "Books"));
        return p;
    }

    @Test
    @DisplayName("GET /products devolve 200 e a lista de produtos")
    void findAllDevolve200() throws Exception {
        when(service.findALl()).thenReturn(List.of(livro()));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("The Lord of the Rings"))
                .andExpect(jsonPath("$[0].price").value(90.5));
    }

    @Test
    @DisplayName("GET /products/{id} devolve o produto com suas categorias")
    void findByIdDevolveOProdutoComCategorias() throws Exception {
        when(service.findById(1L)).thenReturn(livro());

        mockMvc.perform(get("/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Lorem ipsum dolor sit amet."))
                .andExpect(jsonPath("$.categories", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$.categories[0].name").value("Books"));
    }

    @Test
    @DisplayName("o produto nao serializa os pedidos que o contem (evita recursao infinita)")
    void produtoNaoSerializaOsPedidos() throws Exception {
        when(service.findById(1L)).thenReturn(livro());

        mockMvc.perform(get("/products/{id}", 1L))
                .andExpect(jsonPath("$.orders").doesNotExist())
                .andExpect(jsonPath("$.items").doesNotExist());
    }
}
