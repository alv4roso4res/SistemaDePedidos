package com.alvaropaiva.SistemaDePedidos.resources;

import com.alvaropaiva.SistemaDePedidos.entities.Order;
import com.alvaropaiva.SistemaDePedidos.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
/**
 * Controller REST para operações com pedidos.
 *
 * Expõe endpoints para consulta de pedidos, incluindo seus itens e status.
 * Todas as rotas são mapeadas para o caminho base "/orders".
 *
 * Endpoints disponíveis:
 * - GET /orders → Lista todos os pedidos
 * - GET /orders/{id} → Busca pedido por ID (com itens e cliente)
 *
 * Estrutura completa de um pedido (JSON):
 * {
 *   "id": 1,
 *   "moment": "2023-01-01T12:00:00Z",
 *   "orderStatus": 2,
 *   "client": {
 *     "id": 1,
 *     "name": "João Silva"
 *   },
 *   "items": [
 *     {
 *       "quantity": 2,
 *       "price": 999.99,
 *       "product": {
 *         "id": 5,
 *         "name": "Smartphone"
 *       }
 *     }
 *   ],
 *   "payment": {
 *     "moment": "2023-01-01T12:05:00Z"
 *   }
 * }
 */
@RestController
@RequestMapping(value="/orders")
public class OrderResource {

    @Autowired
    private OrderService service;
    /**
     * Lista todos os pedidos do sistema.
     * @return ResponseEntity contendo:
     *         - Lista de pedidos com dados resumidos
     *         - Status HTTP 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List> findAll(){
        List<Order> list = service.findALl();
        return ResponseEntity.ok().body(list);
    }
    /**
     * Busca um pedido completo por ID.
     * @param id Identificador único do pedido
     * @return ResponseEntity contendo:
     *         - Pedido com todos os relacionamentos (itens, cliente, pagamento)
     *         - Status HTTP 200 (OK)
     *         - Status HTTP 404 se não encontrado
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<Order> findById(@PathVariable Long id){
        Order obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

}
