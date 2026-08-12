package com.alvaropaiva.SistemaDePedidos.services;

import com.alvaropaiva.SistemaDePedidos.entities.User;
import com.alvaropaiva.SistemaDePedidos.repositories.UserRepository;
import com.alvaropaiva.SistemaDePedidos.services.exceptions.DataBaseException;
import com.alvaropaiva.SistemaDePedidos.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável pelas operações de negócio relacionadas a usuários.
 *
 * Oferece operações CRUD completas para a entidade {@link User}, integrando-se com:
 * - {@link UserRepository} para persistência
 * - Exceções customizadas para tratamento de erros
 *
 * Principais funcionalidades:
 * - Busca paginada/ordenada (implícita via JpaRepository)
 * - Validação de regras de negócio
 * - Tratamento de exceções de banco de dados
 *
 * @see ResourceNotFoundException Para erros de recurso não encontrado (404)
 * @see DataBaseException Para erros de integridade (500)
 */
@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    /**
     * Retorna todos os usuários cadastrados.
     * @return Lista de usuários (pode ser vazia)
     */
    public List<User> findAll() {
        return repository.findAll();
    }

    /**
     * Busca um usuário por ID.
     * @param id Identificador do usuário
     * @return Entidade User correspondente
     * @throws ResourceNotFoundException Se o ID não existir (HTTP 404)
     */
    public User findById(Long id) {
        Optional<User> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ResourceNotFoundException(id));
    }

    /**
     * Insere um novo usuário no sistema.
     * @param obj Usuário a ser criado (sem ID)
     * @return Usuário criado com ID gerado
     */
    public User insert(User obj) {
        return repository.save(obj);
    }

    /**
     * Remove um usuário do sistema.
     * @param id Identificador do usuário
     * @throws ResourceNotFoundException Se o ID não existir (HTTP 404)
     * @throws DataBaseException Se houver pedidos associados (HTTP 500)
     */
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Cannot delete user: associated orders exist");
        }
    }

    /**
     * Atualiza os dados de um usuário existente.
     * @param id Identificador do usuário
     * @param obj Dados atualizados
     * @return Usuário atualizado
     * @throws ResourceNotFoundException Se o ID não existir (HTTP 404)
     */
    public User update(Long id, User obj) {
        try {
            User entity = repository.getReferenceById(id);
            updateData(entity, obj);
            return repository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    /**
     * Método auxiliar para atualização parcial dos campos.
     * @param entity Entidade existente no banco
     * @param obj Dados novos (não nulos)
     */
    private void updateData(User entity, User obj) {
        if (obj.getName() != null) {
            entity.setName(obj.getName());
        }
        if (obj.getEmail() != null) {
            entity.setEmail(obj.getEmail());
        }
        if (obj.getPhone() != null) {
            entity.setPhone(obj.getPhone());
        }
    }
}