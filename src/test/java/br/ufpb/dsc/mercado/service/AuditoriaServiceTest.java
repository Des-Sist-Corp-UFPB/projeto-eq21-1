package br.ufpb.dsc.mercado.service;

import br.ufpb.dsc.mercado.domain.LogAuditoria;
import br.ufpb.dsc.mercado.repository.LogAuditoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditoriaService — Testes Unitários")
class AuditoriaServiceTest {

    @Mock
    private LogAuditoriaRepository repository;

    @InjectMocks
    private AuditoriaService service;

    @Test
    @DisplayName("registrar: deve salvar log com todos os campos preenchidos")
    void registrar_deveSalvarLogCompleto() {
        when(repository.save(any(LogAuditoria.class))).thenAnswer(inv -> inv.getArgument(0));

        service.registrar("FUNKO", "1", "CRIAR", "admin@test.com", "nome=Batman", "SUCESSO", null);

        ArgumentCaptor<LogAuditoria> captor = ArgumentCaptor.forClass(LogAuditoria.class);
        verify(repository).save(captor.capture());

        LogAuditoria salvo = captor.getValue();
        assertThat(salvo.getRecurso()).isEqualTo("FUNKO");
        assertThat(salvo.getRecursoId()).isEqualTo("1");
        assertThat(salvo.getOperacao()).isEqualTo("CRIAR");
        assertThat(salvo.getUsuario()).isEqualTo("admin@test.com");
        assertThat(salvo.getDetalhe()).isEqualTo("nome=Batman");
        assertThat(salvo.getStatus()).isEqualTo("SUCESSO");
        assertThat(salvo.getMensagemErro()).isNull();
    }

    @Test
    @DisplayName("registrar: deve salvar log de falha com mensagem de erro")
    void registrar_falha_deveSalvarMensagemErro() {
        when(repository.save(any(LogAuditoria.class))).thenAnswer(inv -> inv.getArgument(0));

        service.registrar("FUNKO", "99", "EXCLUIR", "admin@test.com",
                "nome=desconhecido", "FALHA", "Funko não encontrado com id: 99");

        ArgumentCaptor<LogAuditoria> captor = ArgumentCaptor.forClass(LogAuditoria.class);
        verify(repository).save(captor.capture());

        LogAuditoria salvo = captor.getValue();
        assertThat(salvo.getStatus()).isEqualTo("FALHA");
        assertThat(salvo.getMensagemErro()).contains("99");
    }

    @Test
    @DisplayName("registrar: deve aceitar recursoId nulo para operações de autenticação")
    void registrar_semRecursoId_deveSalvarComIdNulo() {
        when(repository.save(any(LogAuditoria.class))).thenAnswer(inv -> inv.getArgument(0));

        service.registrar("USUARIO", null, "LOGIN", "user@test.com",
                "email=user@test.com", "SUCESSO", null);

        ArgumentCaptor<LogAuditoria> captor = ArgumentCaptor.forClass(LogAuditoria.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getRecursoId()).isNull();
        assertThat(captor.getValue().getOperacao()).isEqualTo("LOGIN");
    }

    @Test
    @DisplayName("registrar: deve salvar log de registro de usuário")
    void registrar_registrarUsuario_deveSalvarLog() {
        when(repository.save(any(LogAuditoria.class))).thenAnswer(inv -> inv.getArgument(0));

        service.registrar("USUARIO", null, "REGISTRAR", "novo@test.com",
                "email=novo@test.com", "SUCESSO", null);

        ArgumentCaptor<LogAuditoria> captor = ArgumentCaptor.forClass(LogAuditoria.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getRecurso()).isEqualTo("USUARIO");
        assertThat(captor.getValue().getOperacao()).isEqualTo("REGISTRAR");
    }
}
