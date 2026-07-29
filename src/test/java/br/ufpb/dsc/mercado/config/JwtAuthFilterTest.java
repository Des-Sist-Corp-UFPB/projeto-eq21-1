package br.ufpb.dsc.mercado.config;

import br.ufpb.dsc.mercado.service.JwtService;
import br.ufpb.dsc.mercado.service.UsuarioDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthFilter — Testes Unitários")
class JwtAuthFilterTest {

    @Mock private JwtService jwtService;
    @Mock private UsuarioDetailsService usuarioDetailsService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter filter;

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Sem header Authorization: deve passar o filtro sem autenticar")
    void semHeader_devePassarFiltroSemAutenticar() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Header sem prefixo 'Bearer ': deve passar o filtro sem autenticar")
    void headerSemBearer_devePassarFiltroSemAutenticar() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Token válido: deve autenticar e definir o SecurityContext")
    void tokenValido_deveAutenticarUsuario() throws Exception {
        UserDetails userDetails = new User("admin@test.com", "senha",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(request.getHeader("Authorization")).thenReturn("Bearer token.valido");
        when(jwtService.extrairEmail("token.valido")).thenReturn("admin@test.com");
        when(usuarioDetailsService.loadUserByUsername("admin@test.com")).thenReturn(userDetails);
        when(jwtService.tokenValido("token.valido", userDetails)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
                .isEqualTo("admin@test.com");
    }

    @Test
    @DisplayName("Token inválido (JwtException): deve passar sem autenticar")
    void tokenInvalido_devePassarFiltroSemAutenticar() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token.invalido");
        when(jwtService.extrairEmail("token.invalido"))
                .thenThrow(new JwtException("token inválido"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Token com email nulo: deve passar o filtro sem autenticar")
    void tokenComEmailNulo_devePassarFiltroSemAutenticar() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token.sem.email");
        when(jwtService.extrairEmail("token.sem.email")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
