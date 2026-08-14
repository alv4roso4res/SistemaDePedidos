package com.alvaropaiva.SistemaDePedidos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integracao: sobem a aplicacao inteira sobre o H2 e batem nos
 * endpoints de verdade, passando por controller, service, repositorio e
 * serializacao JSON.
 *
 * Os dados vem do TestConfig, que popula o banco no boot (3 categorias,
 * 5 produtos, 2 usuarios, 3 pedidos, 4 itens e 1 pagamento).
 *
 * Detalhes da configuracao:
 * - @ActiveProfiles("test") tem prioridade sobre um eventual .env com
 *   SPRING_PROFILES_ACTIVE=prod, entao estes testes nunca tocam o banco real.
 * - O banco em memoria e exclusivo desta classe, para que os ids do TestConfig
 *   sejam previsiveis mesmo com outros contextos de teste no mesmo build.
 * - @Transactional desfaz no fim de cada teste o que foi escrito por ele.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:apiintegrationdb")
@Transactional
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /users devolve os usuarios criados pelo TestConfig, sem senha")
    void listaUsuariosSemSenha() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Maria Brown"))
                .andExpect(jsonPath("$[1].name").value("Alex Green"))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("123456"))));
    }

    @Test
    @DisplayName("GET /users/{id} inexistente devolve 404 no formato StandardError")
    void usuarioInexistenteDevolve404() throws Exception {
        mockMvc.perform(get("/users/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.message").value("Resource not found. ID 999"))
                .andExpect(jsonPath("$.path").value("/users/999"));
    }

    @Test
    @DisplayName("POST /users grava o usuario e devolve 201, sem ecoar a senha")
    void criaUsuario() throws Exception {
        String body = """
                {"name":"Joao Silva","email":"joao@exemplo.com","phone":"911111111","password":"segredo"}
                """;

        String location = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Joao Silva"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn().getResponse().getHeader("Location");

        // O recurso criado realmente existe na URI devolvida no header Location
        mockMvc.perform(get(java.net.URI.create(location).getPath()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joao@exemplo.com"));
    }

    @Test
    @DisplayName("GET /orders/{id} devolve o pedido completo com total calculado")
    void pedidoCompletoComTotal() throws Exception {
        // Pedido 1: 2 x 90.50 (The Lord of the Rings) + 1 x 1250.00 (Macbook Pro)
        mockMvc.perform(get("/orders/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderStatus").value("PAID"))
                .andExpect(jsonPath("$.client.name").value("Maria Brown"))
                .andExpect(jsonPath("$.client.password").doesNotExist())
                .andExpect(jsonPath("$.items", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$.total").value(1431.0))
                .andExpect(jsonPath("$.payment").exists());
    }

    @Test
    @DisplayName("GET /orders lista os tres pedidos do TestConfig")
    void listaPedidos() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(3)));
    }

    @Test
    @DisplayName("GET /products/{id} devolve o produto com as categorias associadas")
    void produtoComCategorias() throws Exception {
        mockMvc.perform(get("/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("The Lord of the Rings"))
                .andExpect(jsonPath("$.price").value(90.5))
                .andExpect(jsonPath("$.categories[0].name").value("Books"));
    }

    @Test
    @DisplayName("GET /products lista os cinco produtos do TestConfig")
    void listaProdutos() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(5)));
    }

    @Test
    @DisplayName("GET /categories lista as tres categorias do TestConfig")
    void listaCategorias() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("Electronics"));
    }

    @Test
    @DisplayName("GET /categories/{id} devolve a categoria sem a lista de produtos")
    void categoriaSemProdutos() throws Exception {
        mockMvc.perform(get("/categories/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Books"))
                .andExpect(jsonPath("$.products").doesNotExist());
    }
}
