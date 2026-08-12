package com.alvaropaiva.SistemaDePedidos.repositories;

import com.alvaropaiva.SistemaDePedidos.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório para operações de persistência da entidade {@link Product}.
 *
 * Estende {@link JpaRepository} fornecendo operações CRUD básicas para:
 * - Cadastro, atualização e remoção de produtos
 * - Consultas paginadas e ordenadas
 * - Gerenciamento do relacionamento com categorias
 *
 * Mapeia a entidade {@link Product} para a tabela "tb_products" no banco de dados.
 *
 * Métodos herdados mais utilizados:
 * - save(Product entity): Persiste um novo produto ou atualiza existente
 * - findById(Long id): Busca produto por ID (retorna Optional<Product>)
 * - findAll(): Lista todos os produtos
 * - deleteById(Long id): Remove produto pelo ID
 *
 * Relacionamentos importantes:
 * - Conexão com {@link CategoryRepository} via tabela "tb_product_category"
 * - Vinculação com {@link OrderItemRepository} através de itens de pedido
 *
 * Integração:
 * - Utilizado principalmente por {@link com.alvaropaiva.SistemaDePedidos.services.ProductService}
 * - Relacionado com operações de pedidos ({@link OrderRepository})
 *
 * Exemplo de método customizado (comentado):
 * {@code
 * // List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
 * }
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Implementação automática dos métodos básicos pelo Spring Data JPA
    // Métodos de consulta derivados podem ser adicionados conforme necessidade
}