package com.alvaropaiva.SistemaDePedidos.repositories;

import com.alvaropaiva.SistemaDePedidos.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório para operações de persistência da entidade {@link Category}.
 *
 * Estende {@link JpaRepository} fornecendo automaticamente operações CRUD para:
 * - Criação, leitura, atualização e exclusão de categorias
 * - Busca paginada e ordenada
 * - Contagem de registros
 *
 * Mapeia a entidade {@link Category} para a tabela "tb_category" no banco de dados.
 *
 * Métodos herdados mais utilizados:
 * - save(Category entity): Salva ou atualiza uma categoria
 * - findById(Long id): Busca uma categoria por ID
 * - findAll(): Retorna todas as categorias
 * - deleteById(Long id): Remove uma categoria por ID
 *
 * Uso típico:
 * - Gerenciamento de categorias de produtos
 * - Vinculação com produtos através do relacionamento many-to-many
 *
 * Integração:
 * - Utilizado principalmente por {@link com.alvaropaiva.SistemaDePedidos.services.CategoryService}
 * - Relacionado com {@link ProductRepository} via tabela de associação "tb_product_category"
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Spring Data JPA implementará automaticamente os métodos básicos
    // Exemplo de método customizado:
    // List<Category> findByNameContainingIgnoreCase(String name);
}