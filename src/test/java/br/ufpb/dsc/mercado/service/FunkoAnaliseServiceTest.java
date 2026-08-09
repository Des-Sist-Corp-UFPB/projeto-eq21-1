package br.ufpb.dsc.mercado.service;

import br.ufpb.dsc.mercado.dto.FunkoAnaliseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FunkoAnaliseService — Testes Unitários")
class FunkoAnaliseServiceTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private FunkoAnaliseService service;

    @BeforeEach
    void setUp() {
        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        when(builder.build()).thenReturn(chatClient);
        service = new FunkoAnaliseService(builder, objectMapper);
    }

    @SuppressWarnings("unchecked")
    private void stubRespostaAi(String json) {
        when(chatClient.prompt()
                .system(anyString())
                .user((Consumer<ChatClient.PromptUserSpec>) org.mockito.ArgumentMatchers.any())
                .call()
                .content())
                .thenReturn(json);
    }

    @Test
    @DisplayName("analisarImagem: JSON limpo deve ser parseado corretamente")
    void analisarImagem_jsonLimpo_deveRetornarDados() throws Exception {
        stubRespostaAi("{\"nome\":\"Naruto Uzumaki\",\"franquia\":\"Funko Pop\",\"preco\":49.90}");

        MockMultipartFile imagem = new MockMultipartFile(
                "imagem", "naruto.png", "image/png", "conteudo-fake".getBytes());

        FunkoAnaliseResponse resultado = service.analisarImagem(imagem);

        assertThat(resultado.nome()).isEqualTo("Naruto Uzumaki");
        assertThat(resultado.franquia()).isEqualTo("Funko Pop");
        assertThat(resultado.preco()).isEqualByComparingTo(new BigDecimal("49.90"));
    }

    @Test
    @DisplayName("analisarImagem: JSON com texto ao redor deve ser extraído corretamente")
    void analisarImagem_jsonComTextoAoRedor_deveExtrairJson() throws Exception {
        stubRespostaAi("Aqui está:\n```json\n{\"nome\":\"Batman\",\"franquia\":\"Funko Pop\",\"preco\":89.90}\n```");

        MockMultipartFile imagem = new MockMultipartFile(
                "imagem", "batman.jpg", "image/jpeg", "conteudo-fake".getBytes());

        FunkoAnaliseResponse resultado = service.analisarImagem(imagem);

        assertThat(resultado.nome()).isEqualTo("Batman");
        assertThat(resultado.preco()).isEqualByComparingTo(new BigDecimal("89.90"));
    }

    @Test
    @DisplayName("analisarImagem: JSON com preco inteiro deve ser aceito")
    void analisarImagem_precoInteiro_deveRetornarBigDecimal() throws Exception {
        stubRespostaAi("{\"nome\":\"Goku\",\"franquia\":\"Outro\",\"preco\":55}");

        MockMultipartFile imagem = new MockMultipartFile(
                "imagem", "goku.jpg", "image/jpeg", "bytes".getBytes());

        FunkoAnaliseResponse resultado = service.analisarImagem(imagem);

        assertThat(resultado.nome()).isEqualTo("Goku");
        assertThat(resultado.franquia()).isEqualTo("Outro");
        assertThat(resultado.preco()).isEqualByComparingTo(new BigDecimal("55"));
    }

    @Test
    @DisplayName("analisarImagem: JSON malformado deve lançar exceção")
    void analisarImagem_jsonMalformado_deveLancarExcecao() {
        stubRespostaAi("isso nao é JSON válido sem chaves");

        MockMultipartFile imagem = new MockMultipartFile(
                "imagem", "invalido.png", "image/png", "bytes".getBytes());

        assertThatThrownBy(() -> service.analisarImagem(imagem))
                .isInstanceOf(Exception.class);
    }
}
