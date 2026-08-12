package com.alvaropaiva.SistemaDePedidos.resources.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.Instant;

/**
 * Classe que representa o modelo padrão de resposta de erro da API.
 *
 * Contém informações estruturadas sobre erros que ocorrem durante
 * o processamento de requisições, seguindo as boas práticas de APIs REST.
 *
 * Campos incluídos:
 * - timestamp: Data/hora do erro no formato ISO 8601 (UTC)
 * - status: Código HTTP do erro (ex: 404, 500)
 * - error: Tipo do erro (ex: "Resource Not Found")
 * - message: Descrição detalhada do erro
 * - path: Endpoint onde o erro ocorreu
 *
 * Exemplo de resposta JSON:
 * {
 *   "timestamp": "2023-01-01T12:00:00Z",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Category with id 5 not found",
 *   "path": "/api/categories/5"
 * }
 */
public class StandardError implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM--dd'T'HH:mm:ss'Z'", timezone = "GMT")
    private Instant timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;

    /**
     * Construtor padrão (necessário para serialização)
     */
    public StandardError() {}

    /**
     * Construtor completo
     */
    public StandardError(Instant timestamp, Integer status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // metodos gett e sett

    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}