package br.ufpb.dsc.mercado.service;

import br.ufpb.dsc.mercado.dto.FunkoAnaliseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FunkoAnaliseService {

    private static final String PROMPT_SISTEMA = """
            Você é um especialista em Funko Pop e bonecos colecionáveis.
            Analise a imagem fornecida (foto ou caixa do produto) e retorne EXCLUSIVAMENTE
            um objeto JSON válido com estas chaves:
            - "nome": nome completo do personagem (string)
            - "franquia": use somente um dos valores: "Funko Pop", "Chibi" ou "Outro" (string)
            - "preco": preço sugerido de revenda em reais como número (number, ex: 49.90)
            Não inclua texto, markdown ou comentários fora do JSON.
            Exemplo: {"nome":"Naruto Uzumaki","franquia":"Funko Pop","preco":49.90}
            """;

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public FunkoAnaliseService(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public FunkoAnaliseResponse analisarImagem(MultipartFile imagem) throws IOException {
        var imagemBytes = new ByteArrayResource(imagem.getBytes());
        var media = new Media(MimeType.valueOf(imagem.getContentType()), imagemBytes);

        String resposta = chatClient.prompt()
                .system(PROMPT_SISTEMA)
                .user(u -> u.text("Analise esta imagem de Funko Pop e retorne o JSON.").media(media))
                .call()
                .content();

        return objectMapper.readValue(extrairJson(resposta), FunkoAnaliseResponse.class);
    }

    private String extrairJson(String texto) {
        int inicio = texto.indexOf('{');
        int fim = texto.lastIndexOf('}');
        if (inicio >= 0 && fim > inicio) {
            return texto.substring(inicio, fim + 1);
        }
        return texto;
    }
}
