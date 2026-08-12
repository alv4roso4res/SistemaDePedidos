package com.alvaropaiva.SistemaDePedidos.services.exceptions;

/**
 * Exceção lançada quando um recurso não é encontrado na base de dados.
 *
 * Representa o erro HTTP 404 (Not Found) e é utilizada quando:
 * - Um ID não existe no banco de dados
 * - Uma entidade relacionada obrigatória está ausente
 * - Um recurso necessário para a operação não foi localizado
 *
 * Exemplo de uso típico:
 * {@code
 * public User findById(Long id) {
 *     return userRepository.findById(id)
 *         .orElseThrow(() -> new ResourceNotFoundException(id));
 * }
 * }
 *
 * Herda de RuntimeException para não exigir tratamento obrigatório.
 */
public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constrói a exceção com mensagem padrão contendo o ID não encontrado.
     * @param id Identificador do recurso não encontrado (pode ser qualquer tipo)
     */
    public ResourceNotFoundException(Object id) {
        super("Resource not found. ID " + id);
    }
}