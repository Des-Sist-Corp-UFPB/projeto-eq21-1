package br.ufpb.dsc.mercado.controller;

import br.ufpb.dsc.mercado.config.SecurityConfig;
import br.ufpb.dsc.mercado.dto.TokenResponse;
import br.ufpb.dsc.mercado.exception.EmailJaCadastradoException;
import br.ufpb.dsc.mercado.service.AuthService;
import br.ufpb.dsc.mercado.service.JwtService;
import br.ufpb.dsc.mercado.service.UsuarioDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@DisplayName("AuthController — Testes de Unidade (WebMvcTest)")
class AuthControllerUnitTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean AuthService authService;
    @MockitoBean JwtService jwtService;
    @MockitoBean UsuarioDetailsService usuarioDetailsService;

    @Test
    @DisplayName("POST /auth/register: dados válidos devem retornar 201 com token")
    void register_dadosValidos_deveRetornar201() throws Exception {
        when(authService.registrar(any()))
                .thenReturn(TokenResponse.bearer("tok123", "joao@example.com", "CUSTOMER"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"joao@example.com","senha":"senha123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("tok123"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    @DisplayName("POST /auth/register: e-mail duplicado deve retornar 409")
    void register_emailDuplicado_deveRetornar409() throws Exception {
        when(authService.registrar(any()))
                .thenThrow(new EmailJaCadastradoException("joao@example.com"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"joao@example.com","senha":"senha123"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    @DisplayName("POST /auth/register: senha curta deve retornar 400")
    void register_senhaCurta_deveRetornar400() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"joao@example.com","senha":"123"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.senha").exists());
    }

    @Test
    @DisplayName("POST /auth/register: e-mail inválido deve retornar 400")
    void register_emailInvalido_deveRetornar400() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"nao-e-email","senha":"senha123"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.email").exists());
    }

    @Test
    @DisplayName("POST /auth/login: credenciais válidas devem retornar 200 com token")
    void login_credenciaisValidas_deveRetornar200() throws Exception {
        when(authService.autenticar(any()))
                .thenReturn(TokenResponse.bearer("tok456", "maria@example.com", "CUSTOMER"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"maria@example.com","senha":"senha123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("tok456"))
                .andExpect(jsonPath("$.tipo").value("Bearer"));
    }

    @Test
    @DisplayName("POST /auth/login: credenciais inválidas devem retornar 401")
    void login_credenciaisInvalidas_deveRetornar401() throws Exception {
        when(authService.autenticar(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"maria@example.com","senha":"senhaErrada"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro").exists());
    }
}
