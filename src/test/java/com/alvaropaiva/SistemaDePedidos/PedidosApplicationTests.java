package com.alvaropaiva.SistemaDePedidos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Verifica que o contexto da aplicacao sobe.
 *
 * O perfil "test" e fixado aqui: sem isso, um .env com SPRING_PROFILES_ACTIVE=prod
 * faria o teste subir contra o banco de producao.
 */
@SpringBootTest
@ActiveProfiles("test")
class PedidosApplicationTests {

	@Test
	void contextLoads() {
	}

}
