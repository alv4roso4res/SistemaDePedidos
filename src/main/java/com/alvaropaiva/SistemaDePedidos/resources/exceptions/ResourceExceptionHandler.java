package com.alvaropaiva.SistemaDePedidos.resources.exceptions;

import com.alvaropaiva.SistemaDePedidos.services.exceptions.DataBaseException;
import com.alvaropaiva.SistemaDePedidos.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

/**
 * Classe global de tratamento de exceções da API.
 *
 * Captura exceções lançadas pelos controllers e transforma em respostas
 * HTTP padronizadas usando {@link StandardError}.
 *
 * Principais funcionalidades:
 * - Tratamento centralizado de erros
 * - Respostas consistentes no formato JSON
 * - Mapeamento de exceções para códigos HTTP apropriados
 *
 * Exceções tratadas:
 * 1. {@link ResourceNotFoundException} → HTTP 404 (Not Found)
 * 2. {@link DataBaseException} → HTTP 400 (Bad Request)
 *
 * Exemplo de fluxo:
 * 1. Controller lança exceção
 * 2. Handler captura e cria resposta
 * 3. Cliente recebe JSON padronizado
 */
@ControllerAdvice
public class ResourceExceptionHandler {

    /**
     * Trata exceções quando recursos não são encontrados.
     * @param e Exceção lançada pelo serviço
     * @param request Objeto HttpServletRequest da requisição
     * @return ResponseEntity com StandardError e status HTTP 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        String error = "Resource not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                error,
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    /**
     * Trata exceções de violação de integridade do banco de dados.
     * @param e Exceção lançada pelo serviço
     * @param request Objeto HttpServletRequest da requisição
     * @return ResponseEntity com StandardError e status HTTP 400
     */
    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<StandardError> database(DataBaseException e, HttpServletRequest request) {
        String error = "Database error";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                error,
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }
}