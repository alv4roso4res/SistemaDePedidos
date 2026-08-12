package com.alvaropaiva.SistemaDePedidos.repositories;

import com.alvaropaiva.SistemaDePedidos.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório para operações de persistência relacionadas à entidade {@link User}.
 *
 * Esta interface estende {@link JpaRepository}, fornecendo automaticamente:
 * - Operações CRUD básicas (save, findById, findAll, delete, etc.)
 * - Paginação e ordenação
 * - Contagem de entidades
 *
 * Não é necessário anotar com @Repository pois a extensão de JpaRepository
 * já fornece essa funcionalidade automaticamente.
 *
 * Métodos herdados mais utilizados:
 * - save(User entity): Salva ou atualiza um usuário
 * - findById(Long id): Busca um usuário por ID
 * - findAll(): Retorna todos os usuários
 * - deleteById(Long id): Remove um usuário por ID
 *
 * Integração:
 * - Utilizado principalmente por {@link com.alvaropaiva.SistemaDePedidos.services.UserService}
 * - Mapeia a entidade {@link User} para a tabela "tb_user"
 */
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA implementará automaticamente os métodos básicos
    // Métodos de consulta customizados podem ser adicionados aqui seguindo a convenção de nomes
    // Exemplo: User findByEmail(String email);
}