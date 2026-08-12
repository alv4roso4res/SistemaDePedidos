package com.alvaropaiva.SistemaDePedidos.resources;

import com.alvaropaiva.SistemaDePedidos.entities.Product;
import com.alvaropaiva.SistemaDePedidos.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * Controller REST para operações com produtos.
 *
 * Expõe endpoints para consulta de produtos, seguindo padrões RESTful.
 * Todas as rotas são mapeadas para o caminho base "/products".
 *
 * Endpoints disponíveis:
 * - GET /products → Lista todos os produtos
 * - GET /products/{id} → Busca um produto por ID
 *
 * Relacionamentos importantes:
 * - Cada produto pode pertencer a múltiplas categorias
 * - Pode estar associado a itens de pedido (OrderItem)
 *
 * Exemplo de resposta JSON:
 * {
 *   "id": 1,
 *   "name": "Smartphone",
 *   "description": "Modelo premium",
 *   "price": 2999.99,
 *   "categories": [
 *     {"id": 1, "name": "Eletrônicos"}
 *   ]
 * }
 */
@RestController
@RequestMapping(value="/products")
public class ProductResource {

    @Autowired
    private ProductService service;
    /**
     * Lista todos os produtos cadastrados no sistema.
     * @return ResponseEntity contendo:
     *         - Lista de produtos no corpo (pode ser vazia)
     *         - Status HTTP 200 (OK)
     *         - Status HTTP 404 se nenhum produto for encontrado
     */
    @GetMapping
    public ResponseEntity<List> findAll(){
        List<Product> list = service.findALl();
        return ResponseEntity.ok().body(list);
    }
    /**
     * Busca um produto específico pelo ID.
     * @param id Identificador único do produto (path variable)
     * @return ResponseEntity contendo:
     *         - Produto encontrado com categorias associadas
     *         - Status HTTP 200 (OK)
     *         - Status HTTP 404 se o ID não existir
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id){
        Product obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

}
