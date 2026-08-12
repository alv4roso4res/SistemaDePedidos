package com.alvaropaiva.SistemaDePedidos.services;

import com.alvaropaiva.SistemaDePedidos.entities.Order;
import com.alvaropaiva.SistemaDePedidos.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável pelas operações de negócio relacionadas a pedidos (Orders).
 *
 * Funcionalidades principais:
 * - Consulta de todos os pedidos cadastrados
 * - Busca de pedido específico por ID
 *
 * Integrações:
 * - Utiliza {@link OrderRepository} para operações de persistência
 * - Relaciona-se indiretamente com:
 *   - {@link UserService} (cliente associado ao pedido)
 *   - {@link ProductService} (produtos nos itens do pedido)
 *   - {@link PaymentService} (pagamento associado)
 *
 * Observações:
 * - Um pedido contém itens ({@link com.alvaropaiva.SistemaDePedidos.entities.OrderItem})
 * - O status do pedido é gerenciado pelo enum {@link com.alvaropaiva.SistemaDePedidos.entities.enums.OrderStatus}
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    /**
     * Retorna todos os pedidos cadastrados no sistema.
     * @return Lista de pedidos (pode ser vazia se não houver registros)
     * @apiNote A ordem dos pedidos retornados depende da implementação do JPA
     */
    public List<Order> findALl(){
        return repository.findAll();
    }

    /**
     * Busca um pedido específico pelo seu identificador único.
     * @param id Identificador do pedido
     * @return O pedido encontrado
     * @apiNote Este método retorna diretamente o valor do Optional,
     *          podendo lançar NoSuchElementException se o ID não existir.
     *          Recomenda-se tratamento adequado no controlador.
     */
    public Order findById(Long id){
        Optional<Order> obj = repository.findById(id);
        return obj.get();
    }
}