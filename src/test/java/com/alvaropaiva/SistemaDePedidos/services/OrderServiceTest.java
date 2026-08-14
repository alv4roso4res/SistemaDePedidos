package com.alvaropaiva.SistemaDePedidos.services;

import com.alvaropaiva.SistemaDePedidos.entities.Order;
import com.alvaropaiva.SistemaDePedidos.entities.enums.OrderStatus;
import com.alvaropaiva.SistemaDePedidos.repositories.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Testes unitarios de OrderService (repositorio mockado).
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @InjectMocks
    private OrderService service;

    private Order pedido() {
        return new Order(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID.getCode(), null);
    }

    @Test
    @DisplayName("findALl devolve a lista do repositorio")
    void findAllDevolveAListaDoRepositorio() {
        Order pedido = pedido();
        when(repository.findAll()).thenReturn(List.of(pedido));

        assertThat(service.findALl()).containsExactly(pedido);
    }

    @Test
    @DisplayName("findALl devolve lista vazia quando nao ha pedidos")
    void findAllDevolveListaVazia() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.findALl()).isEmpty();
    }

    @Test
    @DisplayName("findById devolve o pedido quando o id existe")
    void findByIdDevolveOPedido() {
        Order pedido = pedido();
        when(repository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThat(service.findById(1L)).isEqualTo(pedido);
    }

    @Test
    @DisplayName("findById lanca NoSuchElementException quando o id nao existe (vira 500, nao 404)")
    void findByIdComIdInexistenteLancaNoSuchElement() {
        // Comportamento atual, fixado de proposito: diferente de UserService, este
        // service faz Optional.get() direto e nao traduz para ResourceNotFoundException.
        // Quando esse service passar a seguir o padrao de UserService, este teste
        // deve mudar para esperar ResourceNotFoundException.
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
