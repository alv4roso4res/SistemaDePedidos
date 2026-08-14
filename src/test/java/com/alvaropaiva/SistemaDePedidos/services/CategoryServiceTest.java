package com.alvaropaiva.SistemaDePedidos.services;

import com.alvaropaiva.SistemaDePedidos.entities.Category;
import com.alvaropaiva.SistemaDePedidos.repositories.CategoryRepository;
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
 * Testes unitarios de CategoryService (repositorio mockado).
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository repository;

    @InjectMocks
    private CategoryService service;

    @Test
    @DisplayName("findALl devolve a lista do repositorio")
    void findAllDevolveAListaDoRepositorio() {
        when(repository.findAll()).thenReturn(List.of(
                new Category(1L, "Electronics"),
                new Category(2L, "Books")
        ));

        assertThat(service.findALl())
                .extracting(Category::getName)
                .containsExactly("Electronics", "Books");
    }

    @Test
    @DisplayName("findALl devolve lista vazia quando nao ha categorias")
    void findAllDevolveListaVazia() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.findALl()).isEmpty();
    }

    @Test
    @DisplayName("findById devolve a categoria quando o id existe")
    void findByIdDevolveACategoria() {
        when(repository.findById(2L)).thenReturn(Optional.of(new Category(2L, "Books")));

        assertThat(service.findById(2L).getName()).isEqualTo("Books");
    }

    @Test
    @DisplayName("findById lanca NoSuchElementException quando o id nao existe (vira 500, nao 404)")
    void findByIdComIdInexistenteLancaNoSuchElement() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
