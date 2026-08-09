package br.ufpb.dsc.mercado.controller;

import br.ufpb.dsc.mercado.repository.LogAuditoriaRepository;
import br.ufpb.dsc.mercado.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.services.s3.S3Client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@DisplayName("AuthController — Testes de Integração")
class AuthControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @MockitoBean
    private S3Client s3Client;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LogAuditoriaRepository logAuditoriaRepository;

    @BeforeEach
    void setUp() {
        logAuditoriaRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /auth/register: deve cadastrar usuário e retornar token")
    void register_dadosValidos_deveRetornar201ComToken() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"joao@example.com","senha":"senha123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.email").value("joao@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    @DisplayName("POST /auth/register: e-mail duplicado deve retornar 409")
    void register_emailDuplicado_deveRetornar409() throws Exception {
        String body = """
                {"email":"joao@example.com","senha":"senha123"}
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
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
    @DisplayName("POST /auth/login: credenciais válidas devem retornar token")
    void login_credenciaisValidas_deveRetornarToken() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"maria@example.com","senha":"senha123"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"maria@example.com","senha":"senha123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.email").value("maria@example.com"));
    }

    @Test
    @DisplayName("POST /auth/login: senha errada deve retornar 401")
    void login_senhaErrada_deveRetornar401() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"maria@example.com","senha":"senha123"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"maria@example.com","senha":"senhaErrada"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    @DisplayName("POST /auth/login: e-mail inexistente deve retornar 401")
    void login_emailInexistente_deveRetornar401() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"inexistente@example.com","senha":"senha123"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    @DisplayName("Auditoria: deve registrar log de REGISTRAR com sucesso")
    void register_deveGerarLogDeAuditoria() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"auditoria@example.com","senha":"senha123"}
                                """))
                .andExpect(status().isCreated());

        assertThat(logAuditoriaRepository.findByOperacao("REGISTRAR"))
                .isNotEmpty()
                .anyMatch(l ->
                        l.getUsuario().equals("auditoria@example.com") &&
                        l.getStatus().equals("SUCESSO"));
    }

    @Test
    @DisplayName("Auditoria: deve registrar FALHA de LOGIN com senha errada")
    void login_senhaErrada_deveGerarLogDeAuditoriaFalha() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"falha@example.com","senha":"senha123"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"falha@example.com","senha":"senhaErrada"}
                                """))
                .andExpect(status().isUnauthorized());

        assertThat(logAuditoriaRepository.findByOperacao("LOGIN"))
                .isNotEmpty()
                .anyMatch(l ->
                        l.getUsuario().equals("falha@example.com") &&
                        l.getStatus().equals("FALHA"));
    }

    @Test
    @DisplayName("Auditoria: deve registrar FALHA de REGISTRAR com email duplicado")
    void register_emailDuplicado_deveGerarLogDeAuditoriaFalha() throws Exception {
        String body = """
                {"email":"dup@example.com","senha":"senha123"}
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());

        long falhas = logAuditoriaRepository.findByOperacao("REGISTRAR").stream()
                .filter(l -> l.getStatus().equals("FALHA")).count();
        assertThat(falhas).isGreaterThanOrEqualTo(1);
    }
}
