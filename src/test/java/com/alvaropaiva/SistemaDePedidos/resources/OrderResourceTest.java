package com.alvaropaiva.SistemaDePedidos.resources;

import com.alvaropaiva.SistemaDePedidos.entities.Order;
import com.alvaropaiva.SistemaDePedidos.entities.OrderItem;
import com.alvaropaiva.SistemaDePedidos.entities.Product;
import com.alvaropaiva.SistemaDePedidos.entities.User;
import com.alvaropaiva.SistemaDePedidos.entities.enums.OrderStatus;
import com.alvaropaiva.SistemaDePedidos.services.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes da camada web de OrderResource, com foco no formato do JSON: o pedido
 * e serializado com cliente, itens, total calculado e status convertido.
 */
@WebMvcTest(OrderResource.class)
@ActiveProfiles("test")
class OrderResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService service;

    private Order pedidoCompleto() {
        User maria = new User(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");
        Order pedido = new Order(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID.getCode(), maria);

        Product livro = new Product(null, 90.5, "Lorem ipsum dolor sit amet.", "The Lord of the Rings");
        livro.setId(1L);
        pedido.getItems().add(new OrderItem(pedido, livro, 2, 90.5));

        return pedido;
    }

    @Test
    @DisplayName("GET /orders devolve 200 e a lista de pedidos")
    void findAllDevolve200() throws Exception {
        when(service.findALl()).thenReturn(List.of(pedidoCompleto()));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("GET /orders/{id} devolve o pedido com cliente, itens e total")
    void findByIdDevolveOPedidoCompleto() throws Exception {
        when(service.findById(1L)).thenReturn(pedidoCompleto());

        mockMvc.perform(get("/orders/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.client.name").value("Maria Brown"))
                .andExpect(jsonPath("$.items", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].price").value(90.5))
                .andExpect(jsonPath("$.items[0].subTotal").value(181.0))
                .andExpect(jsonPath("$.items[0].product.name").value("The Lord of the Rings"))
                .andExpect(jsonPath("$.total").value(181.0));
    }

    @Test
    @DisplayName("o status sai como nome do enum, nao como codigo numerico")
    void statusSaiComoNomeDoEnum() throws Exception {
        when(service.findById(1L)).thenReturn(pedidoCompleto());

        mockMvc.perform(get("/orders/{id}", 1L))
                .andExpect(jsonPath("$.orderStatus").value("PAID"));
    }

    @Test
    @DisplayName("a senha do cliente nao vaza no pedido aninhado")
    void senhaDoClienteNaoVazaNoPedido() throws Exception {
        when(service.findById(1L)).thenReturn(pedidoCompleto());

        mockMvc.perform(get("/orders/{id}", 1L))
                .andExpect(jsonPath("$.client.password").doesNotExist())
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("123456"))));
    }

    @Test
    @DisplayName("o item nao serializa o pedido de volta (evita recursao infinita)")
    void itemNaoSerializaOPedidoDeVolta() throws Exception {
        when(service.findById(1L)).thenReturn(pedidoCompleto());

        mockMvc.perform(get("/orders/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].order").doesNotExist());
    }

    @Test
    @DisplayName("moment usa o padrao de data atual, com o traco duplo")
    void momentUsaOPadraoDeDataAtual() throws Exception {
        // Fixa o comportamento atual: o @JsonFormat de Order.moment esta com
        // "yyyy-MM--dd" (dois tracos). Quando o padrao for corrigido, este teste
        // deve falhar e ser atualizado para "2019-06-20T19:53:07Z".
        when(service.findById(1L)).thenReturn(pedidoCompleto());

        mockMvc.perform(get("/orders/{id}", 1L))
                .andExpect(jsonPath("$.moment").value("2019-06--20T19:53:07Z"));
    }
}
