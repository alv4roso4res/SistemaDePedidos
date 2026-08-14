package com.alvaropaiva.SistemaDePedidos.entities.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes do enum OrderStatus.
 *
 * O status e persistido como codigo numerico, entao a conversao codigo -> enum
 * e o ponto mais sensivel: um codigo invalido no banco so estoura na hora de
 * serializar a resposta.
 */
class OrderStatusTest {

    @ParameterizedTest(name = "codigo {0} corresponde a {1}")
    @CsvSource({
            "1, WAITING_PAYMENT",
            "2, PAID",
            "3, SHIPPED",
            "4, DELIVERED",
            "5, CANCELED"
    })
    @DisplayName("valueOf converte cada codigo no status correspondente")
    void valueOfConverteCadaCodigo(int code, OrderStatus esperado) {
        assertThat(OrderStatus.valueOf(code)).isEqualTo(esperado);
    }

    @ParameterizedTest(name = "{0} tem codigo {1}")
    @CsvSource({
            "WAITING_PAYMENT, 1",
            "PAID, 2",
            "SHIPPED, 3",
            "DELIVERED, 4",
            "CANCELED, 5"
    })
    @DisplayName("getCode devolve o codigo gravado no banco")
    void getCodeDevolveOCodigoGravado(OrderStatus status, int esperado) {
        assertThat(status.getCode()).isEqualTo(esperado);
    }

    @Test
    @DisplayName("os codigos comecam em 1, nao em 0 (o javadoc de Order diz 0-4 e esta errado)")
    void osCodigosComecamEmUm() {
        assertThat(OrderStatus.WAITING_PAYMENT.getCode()).isEqualTo(1);
        assertThatThrownBy(() -> OrderStatus.valueOf(0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("codigo desconhecido lanca IllegalArgumentException")
    void codigoDesconhecidoLancaExcecao() {
        assertThatThrownBy(() -> OrderStatus.valueOf(99))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OrderStatus");
    }
}
