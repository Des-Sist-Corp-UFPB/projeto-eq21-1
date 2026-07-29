package br.ufpb.dsc.mercado.service;

import br.ufpb.dsc.mercado.domain.Funko;
import br.ufpb.dsc.mercado.dto.FunkoRequest;
import br.ufpb.dsc.mercado.exception.FunkoNaoEncontradoException;
import br.ufpb.dsc.mercado.repository.FunkoRepository;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class FunkoService {

    private final FunkoRepository repository;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.public-endpoint}")
    private String publicEndpoint;

    public FunkoService(FunkoRepository repository, S3Client s3Client) {
        this.repository = repository;
        this.s3Client = s3Client;
    }

    @Transactional(readOnly = true)
    public Page<Funko> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Funko> buscar(String termo, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCaseOrFranquiaContainingIgnoreCase(
                termo, termo, pageable);
    }

    @Transactional(readOnly = true)
    public Funko buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new FunkoNaoEncontradoException(id));
    }

    @Transactional
    @WithSpan("criar-funko")
    public Funko criar(FunkoRequest request, MultipartFile imagem) throws IOException {
        Funko funko = new Funko(request.nome(), request.franquia(), request.preco());
        if (imagem != null && !imagem.isEmpty()) {
            funko.setImagemUrl(uploadImagem(imagem));
        }
        return repository.save(funko);
    }

    @Transactional
    @WithSpan("atualizar-funko")
    public Funko atualizar(Long id, FunkoRequest request, MultipartFile imagem) throws IOException {
        Funko funko = buscarPorId(id);
        funko.setNome(request.nome());
        funko.setFranquia(request.franquia());
        funko.setPreco(request.preco());
        if (imagem != null && !imagem.isEmpty()) {
            funko.setImagemUrl(uploadImagem(imagem));
        }
        return repository.save(funko);
    }

    @Transactional
    public void excluir(Long id) {
        Funko funko = buscarPorId(id);
        repository.delete(funko);
    }

    private String uploadImagem(MultipartFile imagem) throws IOException {
        String extensao = obterExtensao(imagem.getOriginalFilename());
        String chave = "funkos/" + UUID.randomUUID() + extensao;

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
