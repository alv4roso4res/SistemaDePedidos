package com.alvaropaiva.SistemaDePedidos.services;

import com.alvaropaiva.SistemaDePedidos.entities.Category;
import com.alvaropaiva.SistemaDePedidos.entities.Product;
import com.alvaropaiva.SistemaDePedidos.repositories.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Testes unitarios de ProductService (repositorio mockado).
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    private Product produto() {
        // Atencao: o construtor de Product nao recebe id e comeca pela imgUrl
        Product p = new Product(null, 90.5, "Lorem ipsum dolor sit amet.", "The Lord of the Rings");
        p.setId(1L);
        return p;
    }

    @Test
    @DisplayName("findALl devolve a lista do repositorio")
    void findAllDevolveAListaDoRepositorio() {
        Product produto = produto();
        when(repository.findAll()).thenReturn(List.of(produto));

        assertThat(service.findALl()).containsExactly(produto);
    }

    @Test
    @DisplayName("findALl devolve lista vazia quando nao ha produtos")
    void findAllDevolveListaVazia() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.findALl()).isEmpty();
    }

    @Test
    @DisplayName("findById devolve o produto com suas categorias")
    void findByIdDevolveOProdutoComCategorias() {
        Product produto = produto();
        produto.getCategories().add(new Category(2L, "Books"));
        when(repository.findById(1L)).thenReturn(Optional.of(produto));

        Product encontrado = service.findById(1L);

        assertThat(encontrado.getName()).isEqualTo("The Lord of the Rings");
        assertThat(encontrado.getCategories()).extracting(Category::getName).containsExactly("Books");
    }

    @Test
    @DisplayName("findById lanca NoSuchElementException quando o id nao existe (vira 500, nao 404)")
    void findByIdComIdInexistenteLancaNoSuchElement() {
        // Mesmo comportamento de OrderService e CategoryService: Optional.get() direto,
        // sem traducao para ResourceNotFoundException.
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
