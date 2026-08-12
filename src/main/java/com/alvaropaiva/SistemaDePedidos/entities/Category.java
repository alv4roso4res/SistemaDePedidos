package com.alvaropaiva.SistemaDePedidos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
/**
 * Entidade que representa uma categoria de produtos no sistema.
 * Mapeada para a tabela "tb_category" no banco de dados.
 *
 * Uma categoria pode estar associada a múltiplos produtos (relacionamento muitos-para-muitos).
 * Exemplo: Categoria "Eletrônicos" pode conter produtos como "Smartphone", "Notebook", etc.
 */
@Entity
@Table(name = "tb_category")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @JsonIgnore  // Evita serialização JSON circular (produtos não serão incluídos na resposta JSON)
    @ManyToMany(mappedBy = "categories")  // Relacionamento muitos-para-muitos com Product
    private Set<Product> products = new HashSet<>();  // Conjunto de produtos associados a esta categoria

    /**
     * Construtor padrão (JPA)
     */
    public Category() {}

    /**
     * Construtor com parâmetros para criação de uma categoria
     * @param id Identificador único
     * @param name Nome da categoria
     */
    public Category(Long id, String name) {
        this.id = id;
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
    /**
     * Retorna o conjunto de produtos associados a esta categoria
     * @return Set<Product> conjunto de produtos
     */
    public Set<Product> getProducts() {
        return products;
    }

    // equals e hash code
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(getId(), category.getId());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}