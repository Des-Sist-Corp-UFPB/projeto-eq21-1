package br.ufpb.dsc.mercado.controller;

import br.ufpb.dsc.mercado.domain.Funko;
import br.ufpb.dsc.mercado.domain.Role;
import br.ufpb.dsc.mercado.domain.Usuario;
import br.ufpb.dsc.mercado.repository.FunkoRepository;
import br.ufpb.dsc.mercado.repository.LogAuditoriaRepository;
import br.ufpb.dsc.mercado.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.services.s3.S3Client;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@DisplayName("FunkoController — Testes de Integração")
class FunkoControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @MockitoBean
    private S3Client s3Client;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FunkoRepository funkoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LogAuditoriaRepository logAuditoriaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Funko funkoCadastrado;

    @BeforeEach
    void setUp() {
        logAuditoriaRepository.deleteAll();
        funkoRepository.deleteAll();
        usuarioRepository.deleteAll();
        funkoCadastrado = funkoRepository.save(
                new Funko("Batman", "DC Comics", new BigDecimal("89.90")));
    }

    @Test
    @DisplayName("GET /ping: deve retornar status ok")
    void ping_deveRetornarStatusOk() throws Exception {
        mockMvc.perform(get("/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.service").value("eq21"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("GET /api/funkos: deve listar funkos sem autenticação")
    void listar_deveRetornarListaComFunkos() throws Exception {
        mockMvc.perform(get("/api/funkos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome").value("Batman"))
                .andExpect(jsonPath("$.content[0].franquia").value("DC Comics"));
    }

    @Test
    @DisplayName("GET /api/funkos/{id}: deve retornar funko existente")
    void buscarPorId_funkoExistente_deveRetornar200() throws Exception {
        mockMvc.perform(get("/api/funkos/{id}", funkoCadastrado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Batman"))
                .andExpect(jsonPath("$.franquia").value("DC Comics"));
    }

    @Test
    @DisplayName("GET /api/funkos/{id}: id inexistente deve retornar 404")
    void buscarPorId_funkoInexistente_deveRetornar404() throws Exception {
        mockMvc.perform(get("/api/funkos/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/funkos: deve criar funko com dados válidos")
    void criar_dadosValidos_deveRetornar201() throws Exception {
        mockMvc.perform(multipart("/api/funkos")
                        .param("nome", "Spider-Man")
                        .param("franquia", "Marvel")
                        .param("preco", "99.90"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Spider-Man"))
                .andExpect(jsonPath("$.franquia").value("Marvel"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/funkos: dados inválidos devem retornar 400")
    void criar_dadosInvalidos_deveRetornar400() throws Exception {
        mockMvc.perform(multipart("/api/funkos")
                        .param("nome", "")
                        .param("franquia", "Marvel")
                        .param("preco", "-5.00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/funkos/{id}: deve excluir funko existente")
    void excluir_funkoExistente_deveRetornar204() throws Exception {
        mockMvc.perform(delete("/api/funkos/{id}", funkoCadastrado.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/funkos/{id}: funko inexistente deve retornar 404")
    void excluir_funkoInexistente_deveRetornar404() throws Exception {
        mockMvc.perform(delete("/api/funkos/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/funkos?busca=: deve filtrar por nome ou franquia")
    void listar_comBusca_deveFiltrarResultados() throws Exception {
        funkoRepository.save(new Funko("Iron Man", "Marvel", new BigDecimal("119.90")));

        mockMvc.perform(get("/api/funkos").param("busca", "Marvel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].franquia").value("Marvel"));
    }

    @Test
    @DisplayName("POST /api/funkos: sem autenticação deve retornar 401 ou 403")
    void criar_semAutenticacao_deveRetornarErroAcesso() throws Exception {
        mockMvc.perform(multipart("/api/funkos")
                        .param("nome", "Spider-Man")
                        .param("franquia", "Marvel")
                        .param("preco", "99.90"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/funkos/{id}: deve atualizar funko existente")
    void atualizar_funkoExistente_deveRetornar200() throws Exception {
        mockMvc.perform(multipart(HttpMethod.PUT, "/api/funkos/{id}", funkoCadastrado.getId())
                        .param("nome", "Batman Deluxe")
                        .param("franquia", "DC Comics")
                        .param("preco", "129.90"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Batman Deluxe"))
                .andExpect(jsonPath("$.preco").value(129.90));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/funkos/{id}: id inexistente deve retornar 404")
    void atualizar_funkoInexistente_deveRetornar404() throws Exception {
        mockMvc.perform(multipart(HttpMethod.PUT, "/api/funkos/{id}", 9999L)
                        .param("nome", "Batman")
                        .param("franquia", "DC")
                        .param("preco", "89.90"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("DELETE /api/funkos/{id}: CUSTOMER não deve ter acesso")
    void excluir_comRoleCustomer_deveRetornar403() throws Exception {
        mockMvc.perform(delete("/api/funkos/{id}", funkoCadastrado.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Fluxo JWT: deve criar funko autenticado com token real")
    void criarFunko_comJwtValido_deveRetornar201() throws Exception {
        usuarioRepository.save(new Usuario(
                "admin@test.com", passwordEncoder.encode("admin123"), Role.ADMIN));

        String body = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"admin@test.com","senha":"admin123"}
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(body).get("token").asText();

        mockMvc.perform(multipart("/api/funkos")
                        .param("nome", "Iron Man")
                        .param("franquia", "Marvel")
                        .param("preco", "119.90")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Iron Man"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Auditoria: deve registrar log ao criar funko")
    void criar_deveGerarLogDeAuditoria() throws Exception {
        mockMvc.perform(multipart("/api/funkos")
                        .param("nome", "Hulk")
                        .param("franquia", "Marvel")
                        .param("preco", "79.90"))
                .andExpect(status().isCreated());

        assertThat(logAuditoriaRepository.findByOperacao("CRIAR"))
                .isNotEmpty()
                .anyMatch(l -> l.getRecurso().equals("FUNKO") && l.getStatus().equals("SUCESSO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Auditoria: deve registrar log ao excluir funko")
    void excluir_deveGerarLogDeAuditoria() throws Exception {
        mockMvc.perform(delete("/api/funkos/{id}", funkoCadastrado.getId()))
                .andExpect(status().isNoContent());

        assertThat(logAuditoriaRepository.findByOperacao("EXCLUIR"))
                .isNotEmpty()
                .anyMatch(l -> l.getStatus().equals("SUCESSO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Auditoria: deve registrar FALHA ao tentar excluir funko inexistente")
    void excluir_funkoInexistente_deveGerarLogDeAuditoriaFalha() throws Exception {
        mockMvc.perform(delete("/api/funkos/{id}", 9999L))
                .andExpect(status().isNotFound());

        assertThat(logAuditoriaRepository.findByOperacao("EXCLUIR"))
                .isNotEmpty()
                .anyMatch(l -> l.getStatus().equals("FALHA"));
    }
}
