package com.alvaropaiva.SistemaDePedidos.config;

import com.alvaropaiva.SistemaDePedidos.entities.*;
import com.alvaropaiva.SistemaDePedidos.entities.enums.OrderStatus;
import com.alvaropaiva.SistemaDePedidos.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.time.Instant;
import java.util.Arrays;

/**
 * Classe de configuração para inicialização do banco de dados em ambiente local.
 *
 * Responsável por popular o banco com dados mockados quando:
 * - O perfil "dev" ou "test" está ativo (ambos usam H2 em memória)
 * - A aplicação é iniciada
 *
 * Nunca roda no perfil "prod", para não duplicar dados no banco real a cada deploy.
 *
 * Estrutura dos dados criados:
 * - 3 categorias (Electronics, Books, Computers)
 * - 5 produtos com associações a categorias
 * - 2 usuários
 * - 3 pedidos com status variados
 * - 4 itens de pedido
 * - 1 pagamento associado a um pedido
 *
 * Uso:
 * - O perfil "dev" é o padrão, então basta iniciar a aplicação
 * - Os dados são persistidos em memória (H2) e recriados a cada inicialização
 */
@Configuration
@Profile({"dev", "test"})
public class TestConfig implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    /**
     * Método executado automaticamente ao iniciar a aplicação.
     * Popula o banco de dados com entidades de teste inter-relacionadas.
     */
    @Override
    public void run(String... args) throws Exception {
        // Criação de categorias
        Category cat1 = new Category(null, "Electronics");
        Category cat2 = new Category(null, "Books");
        Category cat3 = new Category(null, "Computers");

        // Criação de produtos
        Product p1 = new Product(null, 90.5, "Lorem ipsum dolor sit amet, consectetur.", "The Lord of the Rings");
        Product p2 = new Product(null, 2190.0, "Nulla eu imperdiet purus. Maecenas ante.", "Smart TV");
        Product p3 = new Product(null, 1250.0, "Nam eleifend maximus tortor, at mollis.", "Macbook Pro");
        Product p4 = new Product(null, 1200.0, "Donec aliquet odio ac rhoncus cursus.", "PC Gamer");
        Product p5 = new Product(null, 100.99, "Cras fringilla convallis sem vel faucibus.", "Rails for Dummies");

        categoryRepository.saveAll(Arrays.asList(cat1, cat2, cat3));
        productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));

        // Associação produtos-categorias
        p1.getCategories().add(cat2);
        p2.getCategories().add(cat1);
        p2.getCategories().add(cat3);
        p3.getCategories().add(cat3);
        p4.getCategories().add(cat3);
        p5.getCategories().add(cat2);
        productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));

        // Criação de usuários
        User u1 = new User(null, "Maria Brown", "maria@gmail.com", "988888888", "123456");
        User u2 = new User(null, "Alex Green", "alex@gmail.com", "977777777", "123456");

        // Criação de pedidos
        Order o1 = new Order(null, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID.getCode(), u1);
        Order o2 = new Order(null, Instant.parse("2019-07-21T03:42:10Z"), OrderStatus.WAITING_PAYMENT.getCode(), u2);
        Order o3 = new Order(null, Instant.parse("2019-07-22T15:21:22Z"), OrderStatus.WAITING_PAYMENT.getCode(), u1);

        userRepository.saveAll(Arrays.asList(u1, u2));
        orderRepository.saveAll(Arrays.asList(o1, o2, o3));

        // Criação de itens de pedido
        OrderItem oi1 = new OrderItem(o1, p1, 2, p1.getPrice());
        OrderItem oi2 = new OrderItem(o1, p3, 1, p3.getPrice());
        OrderItem oi3 = new OrderItem(o2, p3, 2, p3.getPrice());
        OrderItem oi4 = new OrderItem(o3, p5, 2, p5.getPrice());

        orderItemRepository.saveAll(Arrays.asList(oi1, oi2, oi3, oi4));

        // Criação de pagamento
        Payment pay1 = new Payment(null, Instant.parse("2019-06-20T19:53:07Z"), o1);
        o1.setPayment(pay1);
        orderRepository.save(o1);
    }
}