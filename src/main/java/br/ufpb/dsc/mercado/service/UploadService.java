package br.ufpb.dsc.mercado.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UploadService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.public-endpoint}")
    private String publicEndpoint;

    public UploadService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public List<String> uploadImagens(List<MultipartFile> imagens) throws IOException {
        List<String> urls = new ArrayList<>();
        for (MultipartFile imagem : imagens) {
            if (imagem != null && !imagem.isEmpty()) {
                urls.add(upload(imagem, "pedidos"));
            }
        }
        return urls;
    }

    public String upload(MultipartFile imagem, String pasta) throws IOException {
        String extensao = obterExtensao(imagem.getOriginalFilename());
        String chave = pasta + "/" + UUID.randomUUID() + extensao;

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(chave)
                        .contentType(imagem.getContentType())
                        .contentLength(imagem.getSize())
                        .build(),
                RequestBody.fromBytes(imagem.getBytes())
        );

        return publicEndpoint + "/" + bucket + "/" + chave;
    }

    private String obterExtensao(String nomeArquivo) {
        if (nomeArquivo == null || !nomeArquivo.contains(".")) {
            return "";
        }
        return nomeArquivo.substring(nomeArquivo.lastIndexOf('.'));
    }
}
