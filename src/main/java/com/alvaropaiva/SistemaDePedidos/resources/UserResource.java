package com.alvaropaiva.SistemaDePedidos.resources;

import com.alvaropaiva.SistemaDePedidos.entities.User;
import com.alvaropaiva.SistemaDePedidos.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
/**
 * Controller REST para operações com usuários.
 *
 * Expõe endpoints completos para CRUD de usuários, seguindo padrões RESTful.
 * Todas as rotas são mapeadas para o caminho base "/users".
 *
 * Endpoints disponíveis:
 * - GET /users → Lista todos os usuários
 * - GET /users/{id} → Busca usuário por ID
 * - POST /users → Cria novo usuário
 * - DELETE /users/{id} → Remove usuário
 * - PUT /users/{id} → Atualiza usuário
 *
 * Relacionamentos importantes:
 * - Um usuário pode ter múltiplos pedidos (Order)
 * - Não expõe a senha nas respostas (@JsonIgnore na entidade)
 *
 * Exemplo de JSON para POST:
 * {
 *   "name": "João Silva",
 *   "email": "joao@email.com",
 *   "phone": "11999999999",
 *   "password": "123456"
 * }
 */
@RestController
@RequestMapping(value="/users")
public class UserResource {

    @Autowired
    private UserService service;
    /**
     * Lista todos os usuários cadastrados.
     * @return ResponseEntity contendo:
     *         - Lista de usuários (sem senhas)
     *         - Status HTTP 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List> findAll(){
        List<User> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }
    /**
     * Busca um usuário específico por ID.
     * @param id Identificador único do usuário
     * @return ResponseEntity contendo:
     *         - Dados do usuário (sem senha)
     *         - Status HTTP 200 (OK)
     *         - Status HTTP 404 se não encontrado
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id){
        User obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }
    /**
     * Cria um novo usuário no sistema.
     * @param obj Dados do usuário a ser criado (no corpo da requisição)
     * @return ResponseEntity contendo:
     *         - URI do novo recurso no header Location
     *         - Dados do usuário criado
     *         - Status HTTP 201 (Created)
     */
    @PostMapping
    public ResponseEntity<User> insert(@RequestBody User obj){
        obj = service.insert(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).body(obj);
    }
    /**
     * Remove um usuário existente.
     * @param id Identificador do usuário a ser removido
     * @return ResponseEntity contendo:
     *         - Status HTTP 204 (No Content) se bem-sucedido
     *         - Status HTTP 404 se o usuário não existir
     *         - Status HTTP 500 se houver pedidos associados
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * Atualiza os dados de um usuário existente.
     * @param id Identificador do usuário a ser atualizado
     * @param obj Novos dados do usuário (no corpo da requisição)
     * @return ResponseEntity contendo:
     *         - Dados atualizados do usuário
     *         - Status HTTP 200 (OK)
     *         - Status HTTP 404 se o usuário não existir
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User obj){
        obj = service.update(id, obj);
        return ResponseEntity.ok().body(obj);
    }

}
