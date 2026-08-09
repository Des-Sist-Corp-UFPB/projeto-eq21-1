package br.ufpb.dsc.mercado.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("SecurityConfig — Testes Unitários dos Beans")
class SecurityConfigTest {

    private final JwtAuthFilter mockFilter = mock(JwtAuthFilter.class);
    private final SecurityConfig config = new SecurityConfig(mockFilter);

    @Test
    @DisplayName("passwordEncoder: deve retornar instância de BCryptPasswordEncoder")
    void passwordEncoder_deveRetornarBCrypt() {
        PasswordEncoder encoder = config.passwordEncoder();

        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    @DisplayName("passwordEncoder: deve codificar e verificar senhas corretamente")
    void passwordEncoder_deveCodificarEVerificarSenha() {
        PasswordEncoder encoder = config.passwordEncoder();
        String hash = encoder.encode("minhaSenha123");

        assertThat(encoder.matches("minhaSenha123", hash)).isTrue();
        assertThat(encoder.matches("senhaErrada", hash)).isFalse();
    }

    @Test
    @DisplayName("corsConfigurationSource: deve retornar configuração de CORS não-nula")
    void corsConfigurationSource_deveRetornarConfiguracao() {
        CorsConfigurationSource source = config.corsConfigurationSource();

        assertThat(source).isNotNull();
    }
}
