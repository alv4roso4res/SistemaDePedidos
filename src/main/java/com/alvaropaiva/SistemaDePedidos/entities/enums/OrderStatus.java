package com.alvaropaiva.SistemaDePedidos.entities.enums;

/**
 * Enum que representa os possíveis status de um pedido (Order) no sistema.
 *
 * Cada status possui um código numérico associado para armazenamento no banco de dados
 * e conversão simplificada. Os valores são:
 *
 * 1 - AGUARDANDO_PAGAMENTO (WAITING_PAYMENT)
 * 2 - PAGO (PAID)
 * 3 - ENVIADO (SHIPPED)
 * 4 - ENTREGUE (DELIVERED)
 * 5 - CANCELADO (CANCELED)
 *
 * A ordem dos valores reflete o fluxo normal do ciclo de vida de um pedido.
 */
public enum OrderStatus {
    WAITING_PAYMENT(1),
    PAID(2),
    SHIPPED(3),
    DELIVERED(4),
    CANCELED(5);

    private int code;

    /**
     * Construtor do enum
     * @param code Código numérico associado ao status
     */
    private OrderStatus(int code) {
        this.code = code;
    }

    /**
     * Retorna o código numérico do status
     * @return int código para armazenamento
     */
    public int getCode() {
        return code;
    }

    /**
     * Converte um código numérico para o enum correspondente
     * @param code Código a ser convertido
     * @return OrderStatus enum correspondente
     * @throws IllegalArgumentException Se o código não corresponder a nenhum status
     */
    public static OrderStatus valueOf(int code) {
        for (OrderStatus value : OrderStatus.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Código de OrderStatus inválido");
    }
}