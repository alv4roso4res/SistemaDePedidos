package com.alvaropaiva.SistemaDePedidos.repositories;

import com.alvaropaiva.SistemaDePedidos.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório para operações de persistência da entidade {@link Order}.
 *
 * Estende {@link JpaRepository} fornecendo operações CRUD para gerenciamento de pedidos,
 * incluindo relações com usuários ({@link User}), itens ({@link OrderItem}) e pagamentos ({@link Payment}).
 *
 * Mapeia a entidade {@link Order} para a tabela "tb_order" no banco de dados.
 *
 * Principais operações herdadas:
 * - save(Order entity): Cria/atualiza pedidos (incluindo cascade para Payment)
 * - findById(Long id): Busca pedido por ID (retorna Optional<Order>)
 * - findAll(): Lista todos os pedidos (com suporte a paginação)
 * - deleteById(Long id): Remove pedido pelo ID
 *
 * Relacionamentos chave:
 * - Um pedido pertence a um {@link User} (client_id)
 * - Contém múltiplos {@link OrderItem} (tb_order_item)
 * - Possui um {@link Payment} associado (tb_payment)
 *
 * Consultas customizadas sugeridas (exemplos):
 * {@code
 * // List<Order> findByClientId(Long clientId);
 * // List<Order> findByOrderStatus(OrderStatus status);
 * // Page<Order> findByMomentBetween(Instant startDate, Instant endDate, Pageable pageable);
 * }
 *
 * Integração:
 * - Utilizado principalmente por {@link com.alvaropaiva.SistemaDePedidos.services.OrderService}
 * - Relacionado com {@link OrderItemRepository} e {@link Payment}
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Implementação automática dos métodos básicos pelo Spring Data JPA
    // Métodos de consulta derivados podem ser adicionados seguindo as convenções de nomenclatura
}