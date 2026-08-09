package br.ufpb.dsc.mercado.service;

import br.ufpb.dsc.mercado.domain.Role;
import br.ufpb.dsc.mercado.domain.Usuario;
import br.ufpb.dsc.mercado.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioDetailsService — Testes Unitários")
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioDetailsService service;

    @Test
    @DisplayName("loadUserByUsername: deve retornar UserDetails para email existente")
    void loadUserByUsername_usuarioExistente_deveRetornarUserDetails() {
        Usuario usuario = new Usuario("user@test.com", "encoded-hash", Role.CUSTOMER);
        when(repository.findByEmail("user@test.com")).thenReturn(Optional.of(usuario));

        UserDetails resultado = service.loadUserByUsername("user@test.com");

        assertThat(resultado.getUsername()).isEqualTo("user@test.com");
        assertThat(resultado.getPassword()).isEqualTo("encoded-hash");
        assertThat(resultado.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
    }

    @Test
    @DisplayName("loadUserByUsername: deve lançar UsernameNotFoundException para email inexistente")
    void loadUserByUsername_usuarioInexistente_deveLancarExcecao() {
        when(repository.findByEmail("nao@existe.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("nao@existe.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("nao@existe.com");
    }

    @Test
    @DisplayName("loadUserByUsername: usuário ADMIN deve ter authority ROLE_ADMIN")
    void loadUserByUsername_usuarioAdmin_deveRetornarAuthorityAdmin() {
        Usuario admin = new Usuario("admin@test.com", "encoded-hash", Role.ADMIN);
        when(repository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));

        UserDetails resultado = service.loadUserByUsername("admin@test.com");

        assertThat(resultado.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
