package br.ufpb.dsc.mercado.service;

import br.ufpb.dsc.mercado.domain.Role;
import br.ufpb.dsc.mercado.domain.Usuario;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JwtService — Testes Unitários")
class JwtServiceTest {

    private static final String SECRET =
            "chave-secreta-de-testes-que-tem-pelo-menos-32-caracteres-para-hmac-sha";

    private JwtService service;
    private UserDetails usuario;

    @BeforeEach
    void setUp() {
        service = new JwtService(SECRET, 86_400_000L);
        usuario = new Usuario("test@example.com", "encoded", Role.CUSTOMER);
    }

    @Test
    @DisplayName("gerarToken: deve retornar token não nulo e não vazio")
    void gerarToken_deveRetornarTokenValido() {
        String token = service.gerarToken(usuario);

        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("gerarToken: tokens de usuários distintos devem ser diferentes")
    void gerarToken_usuariosDiferentes_devemGerarTokensDiferentes() {
        UserDetails outro = new Usuario("outro@example.com", "encoded", Role.CUSTOMER);

        String token1 = service.gerarToken(usuario);
        String token2 = service.gerarToken(outro);

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("extrairEmail: deve extrair o email correto do token")
    void extrairEmail_deveRetornarEmailCorreto() {
        String token = service.gerarToken(usuario);

        String email = service.extrairEmail(token);

        assertThat(email).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("tokenValido: token válido para o mesmo usuário deve retornar true")
    void tokenValido_tokenCorreto_deveRetornarTrue() {
        String token = service.gerarToken(usuario);

        assertThat(service.tokenValido(token, usuario)).isTrue();
    }

    @Test
    @DisplayName("tokenValido: token de outro usuário deve retornar false")
    void tokenValido_tokenDeOutroUsuario_deveRetornarFalse() {
        String token = service.gerarToken(usuario);
        UserDetails outro = new Usuario("outro@example.com", "encoded", Role.CUSTOMER);

        assertThat(service.tokenValido(token, outro)).isFalse();
    }

    @Test
    @DisplayName("tokenValido: token expirado deve lançar JwtException")
    void tokenValido_tokenExpirado_deveLancarExcecao() throws InterruptedException {
        JwtService servicoComExpiracaoCurta = new JwtService(SECRET, 1L);
        String token = servicoComExpiracaoCurta.gerarToken(usuario);

        Thread.sleep(50);

        assertThatThrownBy(() -> servicoComExpiracaoCurta.tokenValido(token, usuario))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("extrairEmail: token inválido deve lançar JwtException")
    void extrairEmail_tokenInvalido_deveLancarExcecao() {
        assertThatThrownBy(() -> service.extrairEmail("token.invalido.qualquer"))
                .isInstanceOf(JwtException.class);
    }
}
