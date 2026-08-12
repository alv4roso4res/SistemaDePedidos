package com.alvaropaiva.SistemaDePedidos.services;

import com.alvaropaiva.SistemaDePedidos.entities.Category;
import com.alvaropaiva.SistemaDePedidos.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável pelas operações de negócio relacionadas a categorias de produtos.
 *
 * Funcionalidades principais:
 * - Consulta de categorias cadastradas
 * - Busca de categoria por identificador único
 *
 * Integração:
 * - Utiliza {@link CategoryRepository} para operações de persistência
 * - Relaciona-se indiretamente com {@link com.alvaropaiva.SistemaDePedidos.services.ProductService}
 *   através da associação muitos-para-muitos entre categorias e produtos
 *
 * Métodos disponíveis:
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    /**
     * Retorna todas as categorias cadastradas no sistema.
     * @return Lista de categorias (pode ser vazia se não houver registros)
     */
    public List<Category> findALl(){
        return repository.findAll();
    }

    /**
     * Busca uma categoria específica pelo seu identificador único.
     * @param id Identificador da categoria
     * @return A categoria encontrada
     * @apiNote Observe que este método retorna diretamente o valor do Optional,
     *          podendo lançar NoSuchElementException se o ID não existir.
     *          Recomenda-se tratamento adequado no controlador.
     */
    public Category findById(Long id){
        Optional<Category> obj = repository.findById(id);
        return obj.get();
    }
}