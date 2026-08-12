package com.alvaropaiva.SistemaDePedidos.entities;

import com.alvaropaiva.SistemaDePedidos.entities.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entidade que representa um pedido (order) no sistema.
 * Mapeada para a tabela "tb_order" no banco de dados.
 *
 * Um pedido contém informações sobre o momento da compra, status,
 * cliente associado, itens do pedido e pagamento. Calcula automaticamente
 * o valor total com base nos itens.
 */
@Entity
@Table(name = "tb_order")
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Data e hora do pedido no formato ISO 8601 (UTC)
     * Exemplo: "2023-01-01T12:00:00Z"
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM--dd'T'HH:mm:ss'Z'", timezone = "GMT")
    private Instant moment;

    /**
     * Status do pedido armazenado como código numérico (ver OrderStatus enum)
     * Valores possíveis: 0-AGUARDANDO_PAGAMENTO, 1-PAGO, 2-ENVIADO, 3-ENTREGUE, 4-CANCELADO
     */
    private Integer orderStatus;

    /**
     * Cliente associado ao pedido (relacionamento muitos-para-um)
     * Chave estrangeira: client_id
     */
    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    /**
     * Itens do pedido (relacionamento um-para-muitos)
     * Mapeado pela chave composta em OrderItem (id.order)
     */
    @OneToMany(mappedBy = "id.order")
    private Set<OrderItem> items = new HashSet<>();

    /**
     * Pagamento associado (relacionamento um-para-um bidirecional)
     * CascadeType.ALL: operações no pedido afetam o pagamento
     */
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    /**
     * Construtor padrão (JPA)
     */
    public Order() {}

    /**
     * Construtor com parâmetros obrigatórios
     * @param id Identificador único
     * @param moment Data/hora do pedido (UTC)
     * @param orderStatus Código numérico do status (ver OrderStatus enum)
     * @param client Cliente associado
     */
    public Order(Long id, Instant moment, Integer orderStatus, User client) {
        this.id = id;
        this.moment = moment;
        setOrderStatus(orderStatus);  // Valida via enum
        this.client = client;
    }

    // Metodos getter e setter
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
    public User getClient() {
        return client;
    }
    public void setClient(User client) {
        this.client = client;
    }

    /**
     * Obtém o status do pedido convertido para enum OrderStatus
     */
    public OrderStatus getOrderStatus() {
        return OrderStatus.valueOf(orderStatus);
    }

    /**
     * Define o status do pedido via código numérico
     * @throws IllegalArgumentException se o código for inválido
     */
    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }
    public Payment getPayment() {
        return payment;
    }
    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    /**
     * Retorna os itens do pedido
     */
    public Set<OrderItem> getItems() {
        return items;
    }

    /**
     * Calcula o valor total do pedido somando os subtotais dos itens
     * @return Double valor total do pedido
     */
    public Double getTotal() {
        double sum = 0.0;
        for (OrderItem x : items) {
            sum += x.getSubTotal();
        }
        return sum;
    }

    // Equals e hashcode
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(getId(), order.getId());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}