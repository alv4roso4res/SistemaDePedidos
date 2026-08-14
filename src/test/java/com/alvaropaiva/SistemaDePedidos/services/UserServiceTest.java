package com.alvaropaiva.SistemaDePedidos.services;

import com.alvaropaiva.SistemaDePedidos.entities.User;
import com.alvaropaiva.SistemaDePedidos.repositories.UserRepository;
import com.alvaropaiva.SistemaDePedidos.services.exceptions.DataBaseException;
import com.alvaropaiva.SistemaDePedidos.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitarios de UserService (repositorio mockado).
 *
 * E o unico service que traduz as excecoes do Spring Data para as excecoes de
 * dominio da aplicacao, entao essa traducao e o que mais importa aqui.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Long ID_EXISTENTE = 1L;
    private static final Long ID_INEXISTENTE = 999L;

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    private User maria;

    @BeforeEach
    void setUp() {
        maria = new User(ID_EXISTENTE, "Maria Brown", "maria@gmail.com", "988888888", "123456");
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("devolve a lista do repositorio")
        void devolveAListaDoRepositorio() {
            when(repository.findAll()).thenReturn(List.of(maria));

            assertThat(service.findAll()).containsExactly(maria);
            verify(repository).findAll();
        }

        @Test
        @DisplayName("devolve lista vazia quando nao ha usuarios")
        void devolveListaVazia() {
            when(repository.findAll()).thenReturn(List.of());

            assertThat(service.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("devolve o usuario quando o id existe")
        void devolveOUsuarioQuandoOIdExiste() {
            when(repository.findById(ID_EXISTENTE)).thenReturn(Optional.of(maria));

            assertThat(service.findById(ID_EXISTENTE)).isEqualTo(maria);
        }

        @Test
        @DisplayName("lanca ResourceNotFoundException quando o id nao existe")
        void lancaResourceNotFoundQuandoOIdNaoExiste() {
            when(repository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(ID_INEXISTENTE))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Resource not found. ID " + ID_INEXISTENTE);
        }
    }

    @Nested
    @DisplayName("insert")
    class Insert {

        @Test
        @DisplayName("delega o save ao repositorio e devolve a entidade salva")
        void delegaOSaveAoRepositorio() {
            User novo = new User(null, "Alex Green", "alex@gmail.com", "977777777", "123456");
            User salvo = new User(2L, "Alex Green", "alex@gmail.com", "977777777", "123456");
            when(repository.save(novo)).thenReturn(salvo);

            User resultado = service.insert(novo);

            assertThat(resultado.getId()).isEqualTo(2L);
            verify(repository).save(novo);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("remove o usuario quando o id existe")
        void removeOUsuarioQuandoOIdExiste() {
            service.delete(ID_EXISTENTE);

            verify(repository).deleteById(ID_EXISTENTE);
        }

        @Test
        @DisplayName("traduz EmptyResultDataAccessException para ResourceNotFoundException (404)")
        void traduzIdInexistenteParaResourceNotFound() {
            doThrow(new EmptyResultDataAccessException(1)).when(repository).deleteById(ID_INEXISTENTE);

            assertThatThrownBy(() -> service.delete(ID_INEXISTENTE))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("traduz DataIntegrityViolationException para DataBaseException (400)")
        void traduzViolacaoDeIntegridadeParaDataBaseException() {
            doThrow(new DataIntegrityViolationException("FK violation"))
                    .when(repository).deleteById(ID_EXISTENTE);

            assertThatThrownBy(() -> service.delete(ID_EXISTENTE))
                    .isInstanceOf(DataBaseException.class)
                    .hasMessageContaining("associated orders");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("atualiza nome, email e telefone")
        void atualizaOsTresCampos() {
            when(repository.getReferenceById(ID_EXISTENTE)).thenReturn(maria);
            when(repository.save(any(User.class))).thenAnswer(returnsFirstArg());

            User novosDados = new User(null, "Maria Silva", "maria.silva@gmail.com", "911111111", null);
            User atualizado = service.update(ID_EXISTENTE, novosDados);

            assertThat(atualizado.getName()).isEqualTo("Maria Silva");
            assertThat(atualizado.getEmail()).isEqualTo("maria.silva@gmail.com");
            assertThat(atualizado.getPhone()).isEqualTo("911111111");
        }

        @Test
        @DisplayName("e uma atualizacao parcial: campos nulos nao apagam os valores existentes")
        void camposNulosNaoApagamOsValoresExistentes() {
            when(repository.getReferenceById(ID_EXISTENTE)).thenReturn(maria);
            when(repository.save(any(User.class))).thenAnswer(returnsFirstArg());

            // So o telefone vem preenchido
            User novosDados = new User(null, null, null, "911111111", null);
            User atualizado = service.update(ID_EXISTENTE, novosDados);

            assertThat(atualizado.getPhone()).isEqualTo("911111111");
            assertThat(atualizado.getName()).isEqualTo("Maria Brown");
            assertThat(atualizado.getEmail()).isEqualTo("maria@gmail.com");
        }

        @Test
        @DisplayName("nunca atualiza a senha, mesmo que ela venha no corpo da requisicao")
        void nuncaAtualizaASenha() {
            when(repository.getReferenceById(ID_EXISTENTE)).thenReturn(maria);
            when(repository.save(any(User.class))).thenAnswer(returnsFirstArg());

            User novosDados = new User(null, "Maria Silva", null, null, "senha-nova");
            User atualizado = service.update(ID_EXISTENTE, novosDados);

            assertThat(atualizado.getPassword()).isEqualTo("123456");
        }

        @Test
        @DisplayName("traduz EntityNotFoundException para ResourceNotFoundException (404)")
        void traduzEntidadeInexistenteParaResourceNotFound() {
            when(repository.getReferenceById(ID_INEXISTENTE)).thenThrow(new EntityNotFoundException());

            assertThatThrownBy(() -> service.update(ID_INEXISTENTE, maria))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(repository, never()).save(any(User.class));
        }
    }
}
