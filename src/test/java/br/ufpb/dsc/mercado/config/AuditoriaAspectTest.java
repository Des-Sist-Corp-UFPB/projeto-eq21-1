package br.ufpb.dsc.mercado.config;

import br.ufpb.dsc.mercado.domain.Funko;
import br.ufpb.dsc.mercado.dto.FunkoRequest;
import br.ufpb.dsc.mercado.dto.LoginRequest;
import br.ufpb.dsc.mercado.dto.RegisterRequest;
import br.ufpb.dsc.mercado.dto.TokenResponse;
import br.ufpb.dsc.mercado.exception.EmailJaCadastradoException;
import br.ufpb.dsc.mercado.exception.FunkoNaoEncontradoException;
import br.ufpb.dsc.mercado.service.AuditoriaService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditoriaAspect — Testes Unitários")
class AuditoriaAspectTest {

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private AuditoriaAspect aspect;

    @Mock
    private ProceedingJoinPoint pjp;

    @Mock
    private Signature signature;

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "admin@test.com", null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("auditarFunko criar: deve registrar SUCESSO com ID do funko criado")
    void auditarFunko_criar_sucesso() throws Throwable {
        Funko funko = new Funko("Batman", "DC", new BigDecimal("89.90"));
        FunkoRequest request = new FunkoRequest("Batman", "DC", new BigDecimal("89.90"));

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("criar");
        when(pjp.getArgs()).thenReturn(new Object[]{request, null});
        when(pjp.proceed()).thenReturn(funko);

        Object resultado = aspect.auditarFunko(pjp);

        assertThat(resultado).isEqualTo(funko);
        verify(auditoriaService).registrar(eq("FUNKO"), isNull(), eq("CRIAR"),
                eq("admin@test.com"), contains("Batman"), eq("SUCESSO"), isNull());
    }

    @Test
    @DisplayName("auditarFunko atualizar: deve registrar SUCESSO com ID do argumento")
    void auditarFunko_atualizar_sucesso() throws Throwable {
        Funko funko = new Funko("Batman v2", "DC", new BigDecimal("99.90"));
        FunkoRequest request = new FunkoRequest("Batman v2", "DC", new BigDecimal("99.90"));

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("atualizar");
        when(pjp.getArgs()).thenReturn(new Object[]{1L, request, null});
        when(pjp.proceed()).thenReturn(funko);

        aspect.auditarFunko(pjp);

        verify(auditoriaService).registrar(eq("FUNKO"), eq("1"), eq("ATUALIZAR"),
                eq("admin@test.com"), any(), eq("SUCESSO"), isNull());
    }

    @Test
    @DisplayName("auditarFunko excluir: deve registrar SUCESSO")
    void auditarFunko_excluir_sucesso() throws Throwable {
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("excluir");
        when(pjp.getArgs()).thenReturn(new Object[]{5L});
        when(pjp.proceed()).thenReturn(null);

        aspect.auditarFunko(pjp);

        verify(auditoriaService).registrar(eq("FUNKO"), eq("5"), eq("EXCLUIR"),
                eq("admin@test.com"), any(), eq("SUCESSO"), isNull());
    }

    @Test
    @DisplayName("auditarFunko excluir: deve registrar FALHA quando funko não encontrado")
    void auditarFunko_excluir_falha() throws Throwable {
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("excluir");
        when(pjp.getArgs()).thenReturn(new Object[]{99L});
        when(pjp.proceed()).thenThrow(new FunkoNaoEncontradoException(99L));

        assertThatThrownBy(() -> aspect.auditarFunko(pjp))
                .isInstanceOf(FunkoNaoEncontradoException.class);

        verify(auditoriaService).registrar(eq("FUNKO"), eq("99"), eq("EXCLUIR"),
                eq("admin@test.com"), any(), eq("FALHA"), anyString());
    }

    @Test
    @DisplayName("auditarFunko: deve usar 'anonimo' quando não há usuário autenticado")
    void auditarFunko_semAutenticacao_deveUsarAnonimo() throws Throwable {
        SecurityContextHolder.clearContext();
        FunkoRequest request = new FunkoRequest("Batman", "DC", new BigDecimal("89.90"));
        Funko funko = new Funko("Batman", "DC", new BigDecimal("89.90"));

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("criar");
        when(pjp.getArgs()).thenReturn(new Object[]{request, null});
        when(pjp.proceed()).thenReturn(funko);

        aspect.auditarFunko(pjp);

        verify(auditoriaService).registrar(eq("FUNKO"), any(), eq("CRIAR"),
                eq("anonimo"), any(), eq("SUCESSO"), isNull());
    }

    @Test
    @DisplayName("auditarAuth login: deve registrar SUCESSO com email do request")
    void auditarAuth_login_sucesso() throws Throwable {
        LoginRequest request = new LoginRequest("user@test.com", "senha123");
        TokenResponse response = TokenResponse.bearer("jwt", "user@test.com", "CUSTOMER");

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("autenticar");
        when(pjp.getArgs()).thenReturn(new Object[]{request});
        when(pjp.proceed()).thenReturn(response);

        Object resultado = aspect.auditarAuth(pjp);

        assertThat(resultado).isEqualTo(response);
        verify(auditoriaService).registrar(eq("USUARIO"), isNull(), eq("LOGIN"),
                eq("user@test.com"), any(), eq("SUCESSO"), isNull());
    }

    @Test
    @DisplayName("auditarAuth registrar: deve registrar SUCESSO")
    void auditarAuth_registrar_sucesso() throws Throwable {
        RegisterRequest request = new RegisterRequest("novo@test.com", "senha123");
        TokenResponse response = TokenResponse.bearer("jwt", "novo@test.com", "CUSTOMER");

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("registrar");
        when(pjp.getArgs()).thenReturn(new Object[]{request});
        when(pjp.proceed()).thenReturn(response);

        aspect.auditarAuth(pjp);

        verify(auditoriaService).registrar(eq("USUARIO"), isNull(), eq("REGISTRAR"),
                eq("novo@test.com"), any(), eq("SUCESSO"), isNull());
    }

    @Test
    @DisplayName("auditarAuth registrar: deve registrar FALHA quando email duplicado")
    void auditarAuth_registrar_falha() throws Throwable {
        RegisterRequest request = new RegisterRequest("dup@test.com", "senha123");

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("registrar");
        when(pjp.getArgs()).thenReturn(new Object[]{request});
        when(pjp.proceed()).thenThrow(new EmailJaCadastradoException("dup@test.com"));

        assertThatThrownBy(() -> aspect.auditarAuth(pjp))
                .isInstanceOf(EmailJaCadastradoException.class);

        verify(auditoriaService).registrar(eq("USUARIO"), isNull(), eq("REGISTRAR"),
                eq("dup@test.com"), any(), eq("FALHA"), anyString());
    }

    @Test
    @DisplayName("auditarAuth login: deve registrar FALHA quando credenciais inválidas")
    void auditarAuth_login_falha() throws Throwable {
        LoginRequest request = new LoginRequest("user@test.com", "senha-errada");

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("autenticar");
        when(pjp.getArgs()).thenReturn(new Object[]{request});
        when(pjp.proceed()).thenThrow(
                new org.springframework.security.authentication.BadCredentialsException("Credenciais inválidas"));

        assertThatThrownBy(() -> aspect.auditarAuth(pjp))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class);

        verify(auditoriaService).registrar(eq("USUARIO"), isNull(), eq("LOGIN"),
                eq("user@test.com"), any(), eq("FALHA"), anyString());
    }

    @Test
    @DisplayName("salvarLog: deve silenciar exceção do AuditoriaService sem propagar")
    void salvarLog_falhaNoServico_naoDevePropagar() throws Throwable {
        FunkoRequest request = new FunkoRequest("Batman", "DC", new BigDecimal("89.90"));
        Funko funko = new Funko("Batman", "DC", new BigDecimal("89.90"));

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("criar");
        when(pjp.getArgs()).thenReturn(new Object[]{request, null});
        when(pjp.proceed()).thenReturn(funko);
        doThrow(new RuntimeException("BD indisponível")).when(auditoriaService)
                .registrar(any(), any(), any(), any(), any(), any(), any());

        Object resultado = aspect.auditarFunko(pjp);

        assertThat(resultado).isEqualTo(funko);
    }
}
