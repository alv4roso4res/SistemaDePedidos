package com.alvaropaiva.SistemaDePedidos.resources;

import com.alvaropaiva.SistemaDePedidos.entities.User;
import com.alvaropaiva.SistemaDePedidos.services.UserService;
import com.alvaropaiva.SistemaDePedidos.services.exceptions.DataBaseException;
import com.alvaropaiva.SistemaDePedidos.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes da camada web de UserResource: rotas, codigos HTTP, formato do JSON e
 * o tratamento de erro do ResourceExceptionHandler. O service e mockado, entao
 * nada aqui toca banco de dados.
 */
@WebMvcTest(UserResource.class)
@ActiveProfiles("test")
class UserResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService service;

    private User maria() {
        return new User(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");
    }

    @Test
    @DisplayName("GET /users devolve 200 e a lista de usuarios")
    void findAllDevolve200EAListaDeUsuarios() throws Exception {
        when(service.findAll()).thenReturn(List.of(maria()));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Maria Brown"))
                .andExpect(jsonPath("$[0].email").value("maria@gmail.com"));
    }

    @Test
    @DisplayName("GET /users nunca devolve a senha no JSON")
    void findAllNuncaDevolveASenha() throws Exception {
        when(service.findAll()).thenReturn(List.of(maria()));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("123456"))));
    }

    @Test
    @DisplayName("GET /users/{id} devolve 200 e o usuario, sem a senha")
    void findByIdDevolve200() throws Exception {
        when(service.findById(1L)).thenReturn(maria());

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Maria Brown"))
                .andExpect(jsonPath("$.phone").value("988888888"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("GET /users/{id} inexistente devolve 404 no formato StandardError")
    void findByIdInexistenteDevolve404() throws Exception {
        when(service.findById(999L)).thenThrow(new ResourceNotFoundException(999L));

        mockMvc.perform(get("/users/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.message").value("Resource not found. ID 999"))
                .andExpect(jsonPath("$.path").value("/users/999"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("POST /users devolve 201 com o header Location")
    void insertDevolve201ComLocation() throws Exception {
        User salvo = new User(2L, "Alex Green", "alex@gmail.com", "977777777", "123456");
        when(service.insert(any(User.class))).thenReturn(salvo);

        String body = """
                {"name":"Alex Green","email":"alex@gmail.com","phone":"977777777","password":"123456"}
                """;

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/users/2")))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Alex Green"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("POST /users aceita a senha no corpo, mesmo sem devolve-la na resposta")
    void insertAceitaASenhaNoCorpo() throws Exception {
        when(service.insert(any(User.class)))
                .thenReturn(new User(2L, "Alex Green", "alex@gmail.com", "977777777", "123456"));

        String body = """
                {"name":"Alex Green","email":"alex@gmail.com","phone":"977777777","password":"segredo"}
                """;

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        // A anotacao no campo e WRITE_ONLY, e nao @JsonIgnore: a senha ainda precisa
        // chegar ao service para ser gravada.
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(service).insert(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("segredo");
    }

    @Test
    @DisplayName("PUT /users/{id} devolve 200 com os dados atualizados")
    void updateDevolve200() throws Exception {
        User atualizado = new User(1L, "Maria Silva", "maria@gmail.com", "911111111", "123456");
        when(service.update(eq(1L), any(User.class))).thenReturn(atualizado);

        String body = """
                {"name":"Maria Silva","phone":"911111111"}
                """;

        mockMvc.perform(put("/users/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maria Silva"))
                .andExpect(jsonPath("$.phone").value("911111111"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("PUT /users/{id} inexistente devolve 404")
    void updateInexistenteDevolve404() throws Exception {
        when(service.update(eq(999L), any(User.class))).thenThrow(new ResourceNotFoundException(999L));

        mockMvc.perform(put("/users/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Fulano\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("DELETE /users/{id} devolve 204 sem corpo")
    void deleteDevolve204() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).delete(1L);
    }

    @Test
    @DisplayName("DELETE /users/{id} inexistente devolve 404")
    void deleteInexistenteDevolve404() throws Exception {
        doThrow(new ResourceNotFoundException(999L)).when(service).delete(999L);

        mockMvc.perform(delete("/users/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"));
    }

    @Test
    @DisplayName("DELETE /users/{id} com pedidos associados devolve 400")
    void deleteComPedidosAssociadosDevolve400() throws Exception {
        doThrow(new DataBaseException("Cannot delete user: associated orders exist"))
                .when(service).delete(1L);

        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Database error"))
                .andExpect(jsonPath("$.path").value("/users/1"));
    }
}
