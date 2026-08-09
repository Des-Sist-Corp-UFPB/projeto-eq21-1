package br.ufpb.dsc.mercado.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UploadService — Testes Unitários")
class UploadServiceTest {

    @Mock
    private S3Client s3Client;

    private UploadService uploadService;

    @BeforeEach
    void setUp() {
        uploadService = new UploadService(s3Client);
        ReflectionTestUtils.setField(uploadService, "bucket", "test-bucket");
        ReflectionTestUtils.setField(uploadService, "publicEndpoint", "http://localhost:9000");
    }

    @Test
    @DisplayName("upload: deve fazer upload e retornar URL pública com extensão correta")
    void upload_deveRetornarUrlPublicaComExtensao() throws IOException {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        MockMultipartFile imagem = new MockMultipartFile(
                "imagem", "funko.png", "image/png", "bytes".getBytes());

        String url = uploadService.upload(imagem, "funkos");

        assertThat(url).startsWith("http://localhost:9000/test-bucket/funkos/");
        assertThat(url).endsWith(".png");
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("upload: arquivo sem extensão deve gerar URL sem extensão")
    void upload_semExtensao_deveGerarUrlSemExtensao() throws IOException {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        MockMultipartFile imagem = new MockMultipartFile(
                "imagem", "semextensao", "image/png", "bytes".getBytes());

        String url = uploadService.upload(imagem, "funkos");

        assertThat(url).startsWith("http://localhost:9000/test-bucket/funkos/");
        assertThat(url).doesNotContain(".");
    }

    @Test
    @DisplayName("upload: nome de arquivo nulo deve gerar URL sem extensão")
    void upload_nomeNulo_deveGerarUrlSemExtensao() throws IOException {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        MockMultipartFile imagem = new MockMultipartFile(
                "imagem", null, "image/png", "bytes".getBytes());

        String url = uploadService.upload(imagem, "funkos");

        assertThat(url).startsWith("http://localhost:9000/test-bucket/funkos/");
    }

    @Test
    @DisplayName("uploadImagens: deve retornar lista de URLs para arquivos não-vazios")
    void uploadImagens_deveRetornarListaDeUrls() throws IOException {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        MockMultipartFile imagem = new MockMultipartFile(
                "imagem", "funko.jpg", "image/jpeg", "bytes".getBytes());

        List<String> urls = uploadService.uploadImagens(List.of(imagem));

        assertThat(urls).hasSize(1);
        assertThat(urls.get(0)).contains("test-bucket");
    }

    @Test
    @DisplayName("uploadImagens: arquivo vazio deve ser ignorado")
    void uploadImagens_arquivoVazio_deveSerIgnorado() throws IOException {
        MockMultipartFile vazio = new MockMultipartFile(
                "imagem", "vazio.png", "image/png", new byte[0]);

        List<String> urls = uploadService.uploadImagens(List.of(vazio));

        assertThat(urls).isEmpty();
    }

    @Test
    @DisplayName("uploadImagens: lista com arquivo válido e vazio deve retornar só URL do válido")
    void uploadImagens_misturado_deveRetornarApenasUrlsValidas() throws IOException {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        MockMultipartFile valido = new MockMultipartFile(
                "imagem", "funko.jpg", "image/jpeg", "bytes".getBytes());
        MockMultipartFile vazio = new MockMultipartFile(
                "imagem2", "vazio.png", "image/png", new byte[0]);

        List<String> urls = uploadService.uploadImagens(List.of(valido, vazio));

        assertThat(urls).hasSize(1);
    }
}
