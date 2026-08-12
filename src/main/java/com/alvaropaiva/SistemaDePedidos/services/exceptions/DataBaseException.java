package com.alvaropaiva.SistemaDePedidos.services.exceptions;

/**
 * Exceção lançada quando ocorre violação de integridade referencial no banco de dados.
 *
 * Representa o erro HTTP 500 (Internal Server Error) e é utilizada para:
 * - Tentativas de exclusão de entidades com relacionamentos obrigatórios
 * - Violações de constraints UNIQUE, FOREIGN KEY, etc.
 * - Problemas de integridade em operações em cascata
 *
 * Exemplo típico:
 * {@code
 * try {
 *     userRepository.deleteById(id);
 * } catch (DataIntegrityViolationException e) {
 *     throw new DataBaseException("Cannot delete: User has active orders");
 * }
 * }
 *
 * Herda de RuntimeException para não exigir tratamento obrigatório em toda a cadeia de chamadas.
 */
public class DataBaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constrói a exceção com mensagem customizada.
     * @param msg Mensagem detalhando a violação de integridade
     *            (ex: "Cannot delete category: associated products exist")
     */
    public DataBaseException(String msg) {
        super(msg);
    }
}