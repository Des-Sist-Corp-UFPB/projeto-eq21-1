package br.ufpb.dsc.mercado.config;

import br.ufpb.dsc.mercado.domain.Role;
import br.ufpb.dsc.mercado.domain.Usuario;
import br.ufpb.dsc.mercado.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DataInitializer — Testes Unitários")
class DataInitializerTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;

    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        dataInitializer = new DataInitializer(usuarioRepository, passwordEncoder);
        ReflectionTestUtils.setField(dataInitializer, "adminEmail", "admin@test.com");
        ReflectionTestUtils.setField(dataInitializer, "adminSenha", "admin123");
    }

    @Test
    @DisplayName("run: admin já existe — não deve criar novo usuário")
    void run_adminExistente_naoDeveCriarAdmin() throws Exception {
        when(usuarioRepository.existsByEmail("admin@test.com")).thenReturn(true);

        dataInitializer.run(null);

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("run: admin inexistente — deve criar e salvar com role ADMIN")
    void run_adminInexistente_deveCriarAdminComRoleAdmin() throws Exception {
        when(usuarioRepository.existsByEmail("admin@test.com")).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("$2a$10$hash");

        dataInitializer.run(null);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario salvo = captor.getValue();
        assertThat(salvo.getEmail()).isEqualTo("admin@test.com");
        assertThat(salvo.getRole()).isEqualTo(Role.ADMIN);
        assertThat(salvo.getPassword()).isEqualTo("$2a$10$hash");
    }
}
