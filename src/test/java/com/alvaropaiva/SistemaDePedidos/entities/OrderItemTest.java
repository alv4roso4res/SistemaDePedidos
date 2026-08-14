package com.alvaropaiva.SistemaDePedidos.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de OrderItem: calculo do subtotal e os getters que atravessam a
 * chave composta (OrderItemPK).
 */
class OrderItemTest {

    private Product produto(Long id, String nome, Double preco) {
        Product p = new Product(null, preco, "descricao", nome);
        p.setId(id);
        return p;
    }

    @Test
    @DisplayName("getSubTotal multiplica preco pela quantidade")
    void getSubTotalMultiplicaPrecoPelaQuantidade() {
        OrderItem item = new OrderItem(new Order(), produto(1L, "Smart TV", 2190.0), 3, 2190.0);

        assertThat(item.getSubTotal()).isEqualTo(6570.0);
    }

    @Test
    @DisplayName("getSubTotal usa o preco historico do item, nao o preco atual do produto")
    void getSubTotalUsaOPrecoHistorico() {
        Product produto = produto(1L, "Macbook Pro", 1250.0);
        // Item comprado por 1000.00; depois o produto subiu para 1250.00
        OrderItem item = new OrderItem(new Order(), produto, 2, 1000.0);

        assertThat(item.getSubTotal()).isEqualTo(2000.0);
        assertThat(produto.getPrice()).isEqualTo(1250.0);
    }

    @Test
    @DisplayName("getOrder e getProduct atravessam a chave composta")
    void gettersAtravessamAChaveComposta() {
        Order pedido = new Order(1L, Instant.parse("2019-06-20T19:53:07Z"), 2, null);
        Product produto = produto(5L, "Rails for Dummies", 100.99);

        OrderItem item = new OrderItem(pedido, produto, 1, 100.99);

        assertThat(item.getOrder()).isSameAs(pedido);
        assertThat(item.getProduct()).isSameAs(produto);
    }

    @Test
    @DisplayName("dois itens sao iguais quando apontam para o mesmo pedido e produto")
    void igualdadeUsaPedidoMaisProduto() {
        Order pedido = new Order(1L, Instant.now(), 1, null);
        Product produto = produto(1L, "The Lord of the Rings", 90.5);

        // Quantidade e preco diferentes de proposito: a identidade e so pedido + produto
        OrderItem a = new OrderItem(pedido, produto, 1, 90.5);
        OrderItem b = new OrderItem(pedido, produto, 9, 10.0);

        assertThat(a).isEqualTo(b);
        assertThat(a).hasSameHashCodeAs(b);
    }
}
