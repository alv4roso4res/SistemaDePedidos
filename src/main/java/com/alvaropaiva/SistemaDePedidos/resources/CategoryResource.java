package com.alvaropaiva.SistemaDePedidos.resources;

import com.alvaropaiva.SistemaDePedidos.entities.Category;
import com.alvaropaiva.SistemaDePedidos.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * Controller REST para operações relacionadas a categorias.
 *
 * Expõe endpoints para consulta de categorias.
 * Mapeia todas as requisições para o caminho base "/categories".
 *
 * Endpoints disponíveis:
 * - GET /categories → Lista todas as categorias
 * - GET /categories/{id} → Busca uma categoria específica por ID
 *
 * Integra-se com {@link CategoryService} para processamento da lógica de negócio.
 *
 * Exemplo de resposta JSON:
 * [
 *   {
 *     "id": 1,
 *     "name": "Eletrônicos",
 *     "products": []
 *   }
 * ]
 */
@RestController
@RequestMapping(value="/categories")
public class CategoryResource {

    @Autowired
    private CategoryService service;
    /**
     * Endpoint para listar todas as categorias cadastradas.
     * @return ResponseEntity contendo:
     *         - Lista de categorias no corpo da resposta
     *         - Status HTTP 200 (OK) em caso de sucesso
     *         - Status HTTP 404 (Not Found) se não houver categorias
     */
    @GetMapping
    public ResponseEntity<List> findAll(){
        List<Category> list = service.findALl();
        return ResponseEntity.ok().body(list);
    }
    /**
     * Endpoint para buscar uma categoria por ID.
     * @param id Identificador único da categoria (path variable)
     * @return ResponseEntity contendo:
     *         - Categoria encontrada no corpo da resposta
     *         - Status HTTP 200 (OK) em caso de sucesso
     *         - Status HTTP 404 (Not Found) se o ID não existir
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<Category> findById(@PathVariable Long id){
        Category obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

}
