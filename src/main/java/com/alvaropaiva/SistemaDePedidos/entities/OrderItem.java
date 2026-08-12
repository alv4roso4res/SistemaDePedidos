package com.alvaropaiva.SistemaDePedidos.entities;

import com.alvaropaiva.SistemaDePedidos.entities.pk.OrderItemPK;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entidade que representa um item de pedido no sistema.
 * Mapeada para a tabela "tb_order_item" no banco de dados.
 *
 * Um OrderItem conecta um {@link Product} a um {@link Order} específico,
 * armazenando a quantidade e o preço no momento da compra.
 * Utiliza uma chave primária composta ({@link OrderItemPK}) que combina
 * pedido e produto.
 */
@Entity
@Table(name = "tb_order_item")
public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Chave primária composta (combinação de Order + Product)
     * Inicializada automaticamente para evitar NPE
     */
    @EmbeddedId
    private OrderItemPK id = new OrderItemPK();
    private Integer quantity;
    private Double price; // preço unitário no momento da compra (pode ser diferente do atual)

    /**
     * Construtor padrão (JPA)
     */
    public OrderItem() {}

    /**
     * Construtor completo para criação de um item de pedido
     * @param order Pedido associado
     * @param product Produto vendido
     * @param quantity Quantidade comprada
     * @param price Preço unitário no momento da venda
     */
    public OrderItem(Order order, Product product, Integer quantity, Double price) {
        super();
        id.setOrder(order);
        id.setProduct(product);
        this.quantity = quantity;
        this.price = price;
    }

    // getters e setters indiretos
    /**
     * Retorna o pedido associado (ignorado na serialização JSON para evitar recursão)
     */
    @JsonIgnore
    public Order getOrder() {
        return id.getOrder();
    }
    public void setOrder(Order order) {
        id.setOrder(order);
    }

    /**
     * Retorna o produto vendido neste item
     */
    public Product getProduct() {
        return id.getProduct();
    }
    public void setProduct(Product product) {
        id.setProduct(product);
    }

    // getters e setter diretos
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Calcula o subtotal deste item (preço × quantidade)
     * @return Double valor total do item
     */
    public Double getSubTotal() {
        return price * quantity;
    }

    // Hash code e equals
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}