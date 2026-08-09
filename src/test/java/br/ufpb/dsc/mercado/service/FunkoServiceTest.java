package br.ufpb.dsc.mercado.service;

import br.ufpb.dsc.mercado.domain.Funko;
import br.ufpb.dsc.mercado.dto.FunkoRequest;
import br.ufpb.dsc.mercado.exception.FunkoNaoEncontradoException;
import br.ufpb.dsc.mercado.repository.FunkoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FunkoService — Testes Unitários")
class FunkoServiceTest {

    @Mock
    private FunkoRepository repository;

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private FunkoService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "bucket", "test-bucket");
        ReflectionTestUtils.setField(service, "publicEndpoint", "http://localhost:9000");
    }

    @Test
    @DisplayName("buscarPorId: deve retornar funko quando existir")
    void buscarPorId_funkoExistente_deveRetornarFunko() {
        Funko funko = new Funko("Batman", "DC Comics", new BigDecimal("89.90"));
        when(repository.findById(1L)).thenReturn(Optional.of(funko));

        Funko resultado = service.buscarPorId(1L);

        assertThat(resultado.getNome()).isEqualTo("Batman");
        assertThat(resultado.getFranquia()).isEqualTo("DC Comics");
    }

    @Test
    @DisplayName("buscarPorId: deve lançar exceção quando funko não existir")
    void buscarPorId_funkoInexistente_deveLancarExcecao() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(FunkoNaoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("criar: deve salvar funko sem imagem")
    void criar_semImagem_deveSalvarFunko() throws Exception {
        FunkoRequest request = new FunkoRequest("Spider-Man", "Marvel", new BigDecimal("99.90"));
        Funko salvo = new Funko("Spider-Man", "Marvel", new BigDecimal("99.90"));
        when(repository.save(any(Funko.class))).thenReturn(salvo);

        Funko resultado = service.criar(request, null);

        assertThat(resultado.getNome()).isEqualTo("Spider-Man");
        verify(repository).save(any(Funko.class));
        verifyNoInteractions(s3Client);
    }

    @Test
    @DisplayName("excluir: deve excluir funko existente")
    void excluir_funkoExistente_deveExcluir() {
        Funko funko = new Funko("Iron Man", "Marvel", new BigDecimal("119.90"));
        when(repository.findById(1L)).thenReturn(Optional.of(funko));

        service.excluir(1L);

        verify(repository).delete(funko);
    }

    @Test
    @DisplayName("excluir: deve lançar exceção para funko inexistente")
    void excluir_funkoInexistente_deveLancarExcecao() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(99L))
                .isInstanceOf(FunkoNaoEncontradoException.class);

        verify(repository, never()).delete(any());
    }

    @Test
    @DisplayName("atualizar: deve atualizar dados do funko")
    void atualizar_dadosValidos_deveAtualizar() throws Exception {
        Funko funko = new Funko("Batman", "DC", new BigDecimal("89.90"));
        FunkoRequest request = new FunkoRequest("Batman v2", "DC Comics", new BigDecimal("99.90"));
        when(repository.findById(1L)).thenReturn(Optional.of(funko));
        when(repository.save(any(Funko.class))).thenAnswer(inv -> inv.getArgument(0));

        Funko resultado = service.atualizar(1L, request, null);

        assertThat(resultado.getNome()).isEqualTo("Batman v2");
        assertThat(resultado.getFranquia()).isEqualTo("DC Comics");
        assertThat(resultado.getPreco()).isEqualByComparingTo("99.90");
    }

    @Test
    @DisplayName("listar: deve retornar página de funkos")
    void listar_deveRetornarPaginaComFunkos() {
        Funko funko = new Funko("Batman", "DC", new BigDecimal("89.90"));
        Page<Funko> pagina = new PageImpl<>(List.of(funko));
        when(repository.findAll(any(Pageable.class))).thenReturn(pagina);

        Page<Funko> resultado = service.listar(Pageable.unpaged());

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNome()).isEqualTo("Batman");
        verify(repository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("listar: deve retornar página vazia quando não há funkos")
    void listar_semFunkos_deveRetornarPaginaVazia() {
        when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        Page<Funko> resultado = service.listar(Pageable.unpaged());

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("buscar: deve filtrar funkos por nome ou franquia")
    void buscar_comTermo_deveRetornarFiltrados() {
        Funko funko = new Funko("Batman", "DC Comics", new BigDecimal("89.90"));
        Page<Funko> pagina = new PageImpl<>(List.of(funko));
        when(repository.findByNomeContainingIgnoreCaseOrFranquiaContainingIgnoreCase(
                eq("DC"), eq("DC"), any(Pageable.class))).thenReturn(pagina);

        Page<Funko> resultado = service.buscar("DC", Pageable.unpaged());

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getFranquia()).isEqualTo("DC Comics");
    }

    @Test
    @DisplayName("buscar: deve retornar vazio quando não encontrar correspondência")
    void buscar_semCorrespondencia_deveRetornarVazio() {
        when(repository.findByNomeContainingIgnoreCaseOrFranquiaContainingIgnoreCase(
                eq("XYZ"), eq("XYZ"), any(Pageable.class))).thenReturn(Page.empty());

        Page<Funko> resultado = service.buscar("XYZ", Pageable.unpaged());

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("criar: deve fazer upload da imagem para o S3 e salvar URL")
    void criar_comImagem_deveUploadarParaS3() throws Exception {
        FunkoRequest request = new FunkoRequest("Spider-Man", "Marvel", new BigDecimal("99.90"));
        MockMultipartFile imagem = new MockMultipartFile(
                "imagem", "spider.png", "image/png", "conteudo-fake".getBytes());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(null);
        when(repository.save(any(Funko.class))).thenAnswer(inv -> inv.getArgument(0));

        Funko resultado = service.criar(request, imagem);

        assertThat(resultado.getImagemUrl())
                .isNotNull()
                .contains("test-bucket")
                .contains("funkos/");
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("atualizar: deve fazer upload da nova imagem para o S3")
    void atualizar_comNovaImagem_deveUploadarParaS3() throws Exception {
        Funko funko = new Funko("Batman", "DC", new BigDecimal("89.90"));
        FunkoRequest request = new FunkoRequest("Batman", "DC", new BigDecimal("89.90"));
        MockMultipartFile imagem = new MockMultipartFile(
                "imagem", "batman.jpg", "image/jpeg", "conteudo-fake".getBytes());
        when(repository.findById(1L)).thenReturn(Optional.of(funko));
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(null);
        when(repository.save(any(Funko.class))).thenAnswer(inv -> inv.getArgument(0));

        Funko resultado = service.atualizar(1L, request, imagem);

        assertThat(resultado.getImagemUrl()).isNotNull().contains("test-bucket");
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("criar: arquivo sem extensão deve gerar chave sem ponto")
    void criar_imagemSemExtensao_deveGerarChaveValida() throws Exception {
        FunkoRequest request = new FunkoRequest("Batman", "DC", new BigDecimal("89.90"));
        MockMultipartFile imagem = new MockMultipartFile(
                "imagem", "arquivo-sem-extensao", "image/png", "conteudo".getBytes());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(null);
        when(repository.save(any(Funko.class))).thenAnswer(inv -> inv.getArgument(0));

        Funko resultado = service.criar(request, imagem);

        assertThat(resultado.getImagemUrl()).isNotNull().startsWith("http://localhost:9000/test-bucket/funkos/");
    }
}
