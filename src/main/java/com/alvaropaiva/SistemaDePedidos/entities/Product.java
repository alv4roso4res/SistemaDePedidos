package com.alvaropaiva.SistemaDePedidos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entidade que representa um produto no sistema.
 * Mapeada para a tabela "tb_products" no banco de dados.
 *
 * Um produto pode pertencer a múltiplas categorias e pode estar
 * em vários itens de pedido (OrderItem). Contém informações básicas
 * como nome, descrição, preço e URL da imagem.
 */
@Entity
@Table(name = "tb_products")
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String imgUrl;

    /**
     * Relacionamento muitos-para-muitos com Category.
     * Utiliza tabela de junção "tb_product_category" com
     * product_id (chave estrangeira para Product) e
     * category_id (chave estrangeira para Category).
     */
    @ManyToMany
    @JoinTable(name = "tb_product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();  // Conjunto de categorias do produto

    /**
     * Relacionamento um-para-muitos com OrderItem (itens de pedido).
     * Mapeado pelo campo "product" na classe OrderItemPK (chave composta).
     */
    @OneToMany(mappedBy = "id.product")
    private Set<OrderItem> items = new HashSet<>();  // Conjunto de itens de pedido associados

    /**
     * Construtor padrão (JPA)
     */
    public Product() {}
    /**
     * Construtor com parâmetros básicos do produto.
     * Observação: O ID não é incluído pois é gerado automaticamente.
     */
    public Product(String imgUrl, Double price, String description, String name) {
        this.imgUrl = imgUrl;
        this.price = price;
        this.description = description;
        this.name = name;
    }

    // Metodos getters e setters
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public String getImgUrl() {
        return imgUrl;
    }
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    /**
     * Retorna as categorias associadas a este produto
     * @return Set<Category> conjunto de categorias
     */
    public Set<Category> getCategories() {
        return categories;
    }

    /**
     * Retorna os pedidos (Orders) que contêm este produto.
     * Ignorado na serialização JSON para evitar recursividade.
     * @return Set<Order> conjunto de pedidos
     */
    @JsonIgnore
    public Set<Order> getOrders() {
        Set<Order> set = new HashSet<>();
        for (OrderItem x : items) {
            set.add(x.getOrder());
        }
        return set;
    }

    // Equals e hash code
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(getId(), product.getId());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}