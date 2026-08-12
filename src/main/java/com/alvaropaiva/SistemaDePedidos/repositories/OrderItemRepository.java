package com.alvaropaiva.SistemaDePedidos.repositories;

import com.alvaropaiva.SistemaDePedidos.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório para operações de persistência da entidade {@link OrderItem}.
 *
 * Estende {@link JpaRepository} fornecendo operações CRUD para itens de pedido,
 * que representam a associação entre {@link Product} e {@link Order} com dados adicionais
 * como quantidade e preço momentâneo.
 *
 * Mapeia a entidade {@link OrderItem} para a tabela "tb_order_item" usando
 * a chave primária composta {@link com.alvaropaiva.SistemaDePedidos.entities.pk.OrderItemPK}.
 *
 * Funcionalidades principais:
 * - save(OrderItem): Persiste itens com preços históricos
 * - findById(OrderItemPK): Busca por chave composta (Order + Product)
 * - deleteByOrderId(Long): Exemplo de método customizado sugerido
 *
 * Relacionamentos críticos:
 * - Muitos-para-um com {@link Order} (order_id)
 * - Muitos-para-um com {@link Product} (product_id)
 *
 * Consultas customizadas úteis (exemplos):
 * {@code
 * // List<OrderItem> findByOrderId(Long orderId);
 * // List<OrderItem> findByProductId(Long productId);
 * // @Query("SELECT obj FROM OrderItem obj WHERE obj.id.order = :order")
 * // List<OrderItem> findItemsByOrder(@Param("order") Order order);
 * }
 *
 * Integração:
 * - Utilizado por {@link com.alvaropaiva.SistemaDePedidos.services.OrderService}
 * - Relacionado com {@link ProductRepository} e {@link OrderRepository}
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Implementação automática dos métodos básicos
    // Observação: Operações por ID usam Long, não OrderItemPK
    // Para buscas pela chave composta, métodos customizados são necessários
}