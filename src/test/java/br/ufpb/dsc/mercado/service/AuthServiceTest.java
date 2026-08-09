package br.ufpb.dsc.mercado.service;

import br.ufpb.dsc.mercado.domain.Role;
import br.ufpb.dsc.mercado.domain.Usuario;
import br.ufpb.dsc.mercado.dto.LoginRequest;
import br.ufpb.dsc.mercado.dto.RegisterRequest;
import br.ufpb.dsc.mercado.dto.TokenResponse;
import br.ufpb.dsc.mercado.exception.EmailJaCadastradoException;
import br.ufpb.dsc.mercado.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — Testes Unitários")
class AuthServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService service;

    @Test
    @DisplayName("registrar: deve criar usuário CUSTOMER e retornar token")
    void registrar_emailNovo_deveRetornarToken() {
        RegisterRequest req = new RegisterRequest("novo@test.com", "senha123");
        when(repository.existsByEmail("novo@test.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("encoded-hash");
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.gerarToken(any())).thenReturn("jwt-token");

        TokenResponse response = service.registrar(req);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.tipo()).isEqualTo("Bearer");
        assertThat(response.email()).isEqualTo("novo@test.com");
        assertThat(response.role()).isEqualTo("CUSTOMER");

        verify(repository).existsByEmail("novo@test.com");
        verify(passwordEncoder).encode("senha123");
        verify(repository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("registrar: deve lançar EmailJaCadastradoException quando email já existe")
    void registrar_emailDuplicado_deveLancarExcecao() {
        RegisterRequest req = new RegisterRequest("dup@test.com", "senha123");
        when(repository.existsByEmail("dup@test.com")).thenReturn(true);

        assertThatThrownBy(() -> service.registrar(req))
                .isInstanceOf(EmailJaCadastradoException.class)
                .hasMessageContaining("dup@test.com");

        verify(repository, never()).save(any());
        verify(jwtService, never()).gerarToken(any());
    }

    @Test
    @DisplayName("autenticar: credenciais corretas devem retornar token")
    void autenticar_credenciaisValidas_deveRetornarToken() {
        Usuario usuario = new Usuario("user@test.com", "encoded-hash", Role.CUSTOMER);
        LoginRequest req = new LoginRequest("user@test.com", "senha123");
        when(repository.findByEmail("user@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "encoded-hash")).thenReturn(true);
        when(jwtService.gerarToken(usuario)).thenReturn("jwt-token");

        TokenResponse response = service.autenticar(req);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.email()).isEqualTo("user@test.com");
        assertThat(response.role()).isEqualTo("CUSTOMER");
    }

    @Test
    @DisplayName("autenticar: email inexistente deve lançar BadCredentialsException")
    void autenticar_emailInexistente_deveLancarExcecao() {
        LoginRequest req = new LoginRequest("nao@existe.com", "senha123");
        when(repository.findByEmail("nao@existe.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.autenticar(req))
                .isInstanceOf(BadCredentialsException.class);

        verify(jwtService, never()).gerarToken(any());
    }

    @Test
    @DisplayName("autenticar: senha errada deve lançar BadCredentialsException")
    void autenticar_senhaErrada_deveLancarExcecao() {
        Usuario usuario = new Usuario("user@test.com", "encoded-hash", Role.CUSTOMER);
        LoginRequest req = new LoginRequest("user@test.com", "senha-errada");
        when(repository.findByEmail("user@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha-errada", "encoded-hash")).thenReturn(false);

        assertThatThrownBy(() -> service.autenticar(req))
                .isInstanceOf(BadCredentialsException.class);

        verify(jwtService, never()).gerarToken(any());
    }

    @Test
    @DisplayName("autenticar: deve funcionar com usuário ADMIN")
    void autenticar_usuarioAdmin_deveRetornarRoleAdmin() {
        Usuario admin = new Usuario("admin@test.com", "encoded-hash", Role.ADMIN);
        LoginRequest req = new LoginRequest("admin@test.com", "senha123");
        when(repository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("senha123", "encoded-hash")).thenReturn(true);
        when(jwtService.gerarToken(admin)).thenReturn("admin-token");

        TokenResponse response = service.autenticar(req);

        assertThat(response.role()).isEqualTo("ADMIN");
        assertThat(response.token()).isEqualTo("admin-token");
    }
}
