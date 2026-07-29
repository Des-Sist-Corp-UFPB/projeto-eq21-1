package br.ufpb.dsc.mercado.controller;

import br.ufpb.dsc.mercado.config.SecurityConfig;
import br.ufpb.dsc.mercado.domain.Funko;
import br.ufpb.dsc.mercado.dto.FunkoAnaliseResponse;
import br.ufpb.dsc.mercado.exception.FunkoNaoEncontradoException;
import br.ufpb.dsc.mercado.service.FunkoAnaliseService;
import br.ufpb.dsc.mercado.service.FunkoService;
import br.ufpb.dsc.mercado.service.JwtService;
import br.ufpb.dsc.mercado.service.UsuarioDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FunkoController.class)
@Import(SecurityConfig.class)
@DisplayName("FunkoController — Testes de Unidade (WebMvcTest)")
class FunkoControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private FunkoService funkoService;
    @MockitoBean private FunkoAnaliseService funkoAnaliseService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private UsuarioDetailsService usuarioDetailsService;

    private Funko funkoBase() {
        return new Funko("Batman", "DC Comics", new BigDecimal("89.90"));
    }

    @Test
    @DisplayName("GET /api/funkos: deve listar funkos sem autenticação")
    void listar_semBusca_deveRetornar200() throws Exception {
        when(funkoService.listar(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(funkoBase())));

        mockMvc.perform(get("/api/funkos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Batman"));
    }

    @Test
    @DisplayName("GET /api/funkos?busca=Batman: deve filtrar por nome/franquia")
    void listar_comBusca_deveRetornar200() throws Exception {
        when(funkoService.buscar(eq("Batman"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(funkoBase())));

        mockMvc.perform(get("/api/funkos").param("busca", "Batman"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].franquia").value("DC Comics"));
    }

    @Test
    @DisplayName("GET /api/funkos/{id}: funko existente deve retornar 200")
    void buscarPorId_existente_deveRetornar200() throws Exception {
        when(funkoService.buscarPorId(1L)).thenReturn(funkoBase());

        mockMvc.perform(get("/api/funkos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Batman"));
    }

    @Test
    @DisplayName("GET /api/funkos/{id}: funko inexistente deve retornar 404")
    void buscarPorId_inexistente_deveRetornar404() throws Exception {
        when(funkoService.buscarPorId(999L))
                .thenThrow(new FunkoNaoEncontradoException(999L));

        mockMvc.perform(get("/api/funkos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/funkos: dados válidos devem retornar 201")
    void criar_dadosValidos_deveRetornar201() throws Exception {
        when(funkoService.criar(any(), any()))
                .thenReturn(new Funko("Spider-Man", "Marvel", new BigDecimal("99.90")));

        mockMvc.perform(multipart("/api/funkos")
                        .param("nome", "Spider-Man")
                        .param("franquia", "Marvel")
                        .param("preco", "99.90"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Spider-Man"));
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
    @DisplayName("PUT /api/funkos/{id}: deve atualizar e retornar 200")
    void atualizar_dadosValidos_deveRetornar200() throws Exception {
        when(funkoService.atualizar(eq(1L), any(), any()))
                .thenReturn(new Funko("Batman Deluxe", "DC Comics", new BigDecimal("129.90")));

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/funkos/1")
                        .param("nome", "Batman Deluxe")
                        .param("franquia", "DC Comics")
                        .param("preco", "129.90"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Batman Deluxe"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/funkos/{id}: deve excluir e retornar 204")
    void excluir_deveRetornar204() throws Exception {
        mockMvc.perform(delete("/api/funkos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/funkos/{id}: sem autenticação deve retornar 401/403")
    void excluir_semAutenticacao_deveRetornarErroAcesso() throws Exception {
        mockMvc.perform(delete("/api/funkos/1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/funkos/analyze-image: deve retornar análise com 200")
    void analisarImagem_deveRetornar200() throws Exception {
        var analise = new FunkoAnaliseResponse("Naruto", "Funko Pop", new BigDecimal("49.90"));
        when(funkoAnaliseService.analisarImagem(any())).thenReturn(analise);

        MockMultipartFile imagem = new MockMultipartFile(
                "imagem", "naruto.png", "image/png", "bytes".getBytes());

        mockMvc.perform(multipart("/api/funkos/analyze-image").file(imagem))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Naruto"))
                .andExpect(jsonPath("$.franquia").value("Funko Pop"));
    }
}
