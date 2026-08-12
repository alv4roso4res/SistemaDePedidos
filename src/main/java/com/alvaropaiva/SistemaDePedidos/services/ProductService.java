package com.alvaropaiva.SistemaDePedidos.services;

import com.alvaropaiva.SistemaDePedidos.entities.Product;
import com.alvaropaiva.SistemaDePedidos.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável pelas operações de negócio relacionadas a produtos.
 *
 * Funcionalidades principais:
 * - Consulta de todos os produtos cadastrados
 * - Busca de produto específico por ID
 *
 * Integrações:
 * - Utiliza {@link ProductRepository} para operações de persistência
 * - Relaciona-se com:
 *   - {@link CategoryService} através do relacionamento muitos-para-muitos
 *   - {@link OrderItemService} (indiretamente via itens de pedido)
 *
 * Observações:
 * - Cada produto pode pertencer a múltiplas categorias
 * - O preço do produto pode variar em diferentes pedidos (preço histórico)
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    /**
     * Retorna todos os produtos cadastrados no sistema.
     * @return Lista de produtos (pode ser vazia se não houver registros)
     * @apiNote Inclui produtos com e sem categorias associadas
     */
    public List<Product> findALl() {
        return repository.findAll();
    }

    /**
     * Busca um produto específico pelo seu identificador único.
     * @param id Identificador do produto
     * @return O produto encontrado
     * @apiNote Este método retorna diretamente o valor do Optional,
     *          podendo lançar NoSuchElementException se o ID não existir.
     *          Recomenda-se tratamento adequado no controlador.
     *          O produto retornado inclui suas categorias associadas (se existirem)
     */
    public Product findById(Long id) {
        Optional<Product> obj = repository.findById(id);
        return obj.get();
    }
}