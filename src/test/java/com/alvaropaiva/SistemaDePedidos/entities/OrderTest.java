package com.alvaropaiva.SistemaDePedidos.entities;

import com.alvaropaiva.SistemaDePedidos.entities.enums.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de Order: total calculado no dominio (nao armazenado no banco) e
 * conversao do status entre codigo numerico e enum.
 */
class  OrderTest {

    private Product produto(Long id, String nome, Double preco) {
        Product p = new Product(null, preco, "descricao", nome);
        p.setId(id);
        return p;
    }

    private Order pedido() {
        return new Order(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID.getCode(), null);
    }

    @Test
    @DisplayName("getTotal soma o subtotal de todos os itens")
    void getTotalSomaOsSubtotais() {
        Order pedido = pedido();
        pedido.getItems().add(new OrderItem(pedido, produto(1L, "The Lord of the Rings", 90.5), 2, 90.5));
        pedido.getItems().add(new OrderItem(pedido, produto(3L, "Macbook Pro", 1250.0), 1, 1250.0));

        // 90.5 * 2 + 1250.0 * 1
        assertThat(pedido.getTotal()).isEqualTo(1431.0);
    }

    @Test
    @DisplayName("getTotal devolve zero quando o pedido nao tem itens")
    void getTotalDeUmPedidoVazioEZero() {
        assertThat(pedido().getTotal()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("getOrderStatus converte o codigo gravado no banco para o enum")
    void getOrderStatusConverteOCodigo() {
        Order pedido = new Order(1L, Instant.now(), 2, null);

        assertThat(pedido.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("setOrderStatus aceita o codigo e getOrderStatus devolve o enum correspondente")
    void setOrderStatusTrocaOStatus() {
        Order pedido = pedido();

        pedido.setOrderStatus(OrderStatus.CANCELED.getCode());

        assertThat(pedido.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("codigo de status invalido so estoura na leitura, nao na escrita")
    void codigoInvalidoEstouraNaLeitura() {
        Order pedido = pedido();

        // setOrderStatus nao valida nada, apesar do que diz o javadoc
        pedido.setOrderStatus(99);

        assertThatThrownBy(pedido::getOrderStatus)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("dois pedidos sao iguais quando tem o mesmo id")
    void igualdadeUsaOId() {
        Order a = new Order(1L, Instant.parse("2019-06-20T19:53:07Z"), 1, null);
        Order b = new Order(1L, Instant.parse("2020-01-01T00:00:00Z"), 5, null);

        assertThat(a).isEqualTo(b);
        assertThat(a).hasSameHashCodeAs(b);
    }
}
