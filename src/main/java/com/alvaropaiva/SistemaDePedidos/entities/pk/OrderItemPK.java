package com.alvaropaiva.SistemaDePedidos.entities.pk;

import com.alvaropaiva.SistemaDePedidos.entities.Order;
import com.alvaropaiva.SistemaDePedidos.entities.Product;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

/**
 * Classe que representa a chave primária composta para a entidade OrderItem.
 *
 * Esta classe é usada como ID embutido em {@link com.alvaropaiva.SistemaDePedidos.entities.OrderItem}
 * e combina duas chaves estrangeiras:
 * - order: Referência ao pedido
 * - product: Referência ao produto
 *
 * A combinação order + product deve ser única no sistema.
 */
@Embeddable  // Indica que esta classe pode ser embutida em outra entidade
public class OrderItemPK implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Relacionamento muitos-para-um com Order.
     * Mapeado para a coluna order_id na tabela tb_order_item.
     */
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    /**
     * Relacionamento muitos-para-um com Product.
     * Mapeado para a coluna product_id na tabela tb_order_item.
     */
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // Metodos getter e setter
    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }

    // equals e hash code
    /**
     * Compara duas chaves primárias compostas.
     * Duas OrderItemPK são iguais se tiverem o mesmo Order e Product.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemPK that = (OrderItemPK) o;
        return Objects.equals(getOrder(), that.getOrder()) &&
                Objects.equals(getProduct(), that.getProduct());
    }
    /**
     * Gera um hash code baseado nos objetos Order e Product.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getOrder(), getProduct());
    }
}