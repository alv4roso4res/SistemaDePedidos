package com.alvaropaiva.SistemaDePedidos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Entidade que representa um pagamento no sistema.
 * Mapeada para a tabela "tb_payment" no banco de dados.
 *
 * Um pagamento está associado a exatamente um {@link Order} (pedido)
 * através de uma relação um-para-um compartilhando a mesma chave primária.
 * Registra o momento em que o pagamento foi processado.
 */
@Entity
@Table(name = "tb_payment")
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Identificador único (compartilhado com o pedido associado)
    private Instant moment; // Data e hora do processamento do pagamento

    /**
     * Relacionamento um-para-um bidirecional com Order.
     * - @MapsId indica que o ID do Payment será o mesmo do Order
     * - @JsonIgnore evite serialização JSON circular
     */
    @JsonIgnore
    @OneToOne
    @MapsId
    private Order order;

    /**
     * Construtor padrão (JPA)
     */
    public Payment() {}

    /**
     * Construtor com parâmetros
     * @param id Identificador único (deve coincidir com o ID do Order associado)
     * @param moment Momento do processamento do pagamento
     * @param order Pedido associado a este pagamento
     */
    public Payment(Long id, Instant moment, Order order) {
        this.id = id;
        this.moment = moment;
        this.order = order;
    }

    // getter e setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Instant getMoment() {
        return moment;
    }
    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    /**
     * Retorna o pedido associado a este pagamento
     * @return Order pedido relacionado
     */
    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }


    // hash code e equals
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(getId(), payment.getId());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}