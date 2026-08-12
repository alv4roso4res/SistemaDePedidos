package com.alvaropaiva.SistemaDePedidos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidade que representa um usuário do sistema.
 * Mapeada para a tabela "tb_user" no banco de dados.
 *
 * Um usuário pode ser cliente que realiza pedidos (orders) no sistema.
 * Contém informações básicas como nome, email, telefone e senha.
 *
 * Relacionamentos:
 * - Um-para-muitos com Order (um usuário pode ter vários pedidos)
 */
@Entity
@Table(name = "tb_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String password;

    @JsonIgnore  // Evita serialização JSON infinita
    @OneToMany(mappedBy = "client")  // Relacionamento com a entidade Order
    private List<Order> orders = new ArrayList<>();  // Lista de pedidos associados a esse usuário

    /**
     * Construtor padrão (necessário para o JPA)
     */
    public User() {}

    /**
     * Construtor com parâmetros para criação de um usuário
     * @param id Identificador único
     * @param name Nome do usuário
     * @param email E-mail do usuário
     * @param phone Telefone do usuário
     * @param password Senha do usuário
     */
    public User(Long id, String name, String email, String phone, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    // Métodos Getters e Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Retorna a lista de pedidos associados a este usuário
     * @return List<Order> lista de pedidos
     */
    public List<Order> getOrders() {
        return orders;
    }

    // Equals e hashcode
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}