package br.ufpb.dsc.mercado.service;

import br.ufpb.dsc.mercado.domain.Funko;
import br.ufpb.dsc.mercado.dto.FunkoRequest;
import br.ufpb.dsc.mercado.exception.FunkoNaoEncontradoException;
import br.ufpb.dsc.mercado.repository.FunkoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3Client;

import java.math.BigDecimal;
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
}
