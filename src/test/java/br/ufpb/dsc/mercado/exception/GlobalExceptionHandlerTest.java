package br.ufpb.dsc.mercado.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler — Testes Unitários")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("FunkoNaoEncontradoException deve retornar 404 com chave 'erro'")
    void handleFunkoNaoEncontrado_deveRetornar404() {
        var ex = new FunkoNaoEncontradoException(99L);

        ResponseEntity<Map<String, String>> resp = handler.handleFunkoNaoEncontrado(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody()).containsKey("erro");
        assertThat(resp.getBody().get("erro")).contains("99");
    }

    @Test
    @DisplayName("EmailJaCadastradoException deve retornar 409 com chave 'erro'")
    void handleEmailJaCadastrado_deveRetornar409() {
        var ex = new EmailJaCadastradoException("teste@example.com");

        ResponseEntity<Map<String, String>> resp = handler.handleEmailJaCadastrado(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resp.getBody()).containsKey("erro");
    }

    @Test
    @DisplayName("BadCredentialsException deve retornar 401 com chave 'erro'")
    void handleCredenciaisInvalidas_deveRetornar401() {
        var ex = new BadCredentialsException("Credenciais inválidas");

        ResponseEntity<Map<String, String>> resp = handler.handleCredenciaisInvalidas(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(resp.getBody()).containsKey("erro");
    }

    @Test
    @DisplayName("MethodArgumentNotValidException deve retornar 400 com 'erro' e 'campos'")
    void handleValidacao_deveRetornar400ComCampos() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("funkoRequest", "nome", "não pode ser vazio");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, Object>> resp = handler.handleValidacao(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).containsKey("erro");
        assertThat(resp.getBody()).containsKey("campos");
        @SuppressWarnings("unchecked")
        Map<String, String> campos = (Map<String, String>) resp.getBody().get("campos");
        assertThat(campos).containsKey("nome");
    }
}
