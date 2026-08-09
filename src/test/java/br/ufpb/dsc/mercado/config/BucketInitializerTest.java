package br.ufpb.dsc.mercado.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BucketInitializer — Testes Unitários")
class BucketInitializerTest {

    @Mock
    private S3Client s3Client;

    private BucketInitializer bucketInitializer;

    @BeforeEach
    void setUp() {
        bucketInitializer = new BucketInitializer(s3Client);
        ReflectionTestUtils.setField(bucketInitializer, "bucket", "test-bucket");
    }

    @Test
    @DisplayName("run: bucket já existe — não deve criar novo bucket")
    void run_bucketExistente_naoDeveCriarBucket() throws Exception {
        when(s3Client.headBucket(any(HeadBucketRequest.class)))
                .thenReturn(HeadBucketResponse.builder().build());
        when(s3Client.putBucketPolicy(any(PutBucketPolicyRequest.class)))
                .thenReturn(PutBucketPolicyResponse.builder().build());

        bucketInitializer.run(null);

        verify(s3Client, never()).createBucket(any(CreateBucketRequest.class));
        verify(s3Client).putBucketPolicy(any(PutBucketPolicyRequest.class));
    }

    @Test
    @DisplayName("run: bucket inexistente — deve criar o bucket e aplicar política")
    void run_bucketInexistente_deveCriarBucket() throws Exception {
        when(s3Client.headBucket(any(HeadBucketRequest.class)))
                .thenThrow(NoSuchBucketException.builder().build());
        when(s3Client.createBucket(any(CreateBucketRequest.class)))
                .thenReturn(CreateBucketResponse.builder().build());
        when(s3Client.putBucketPolicy(any(PutBucketPolicyRequest.class)))
                .thenReturn(PutBucketPolicyResponse.builder().build());

        bucketInitializer.run(null);

        verify(s3Client).createBucket(any(CreateBucketRequest.class));
        verify(s3Client).putBucketPolicy(any(PutBucketPolicyRequest.class));
    }

    @Test
    @DisplayName("run: erro de conexão com S3 — deve apenas logar aviso, sem lançar exceção")
    void run_erroDeConexao_deveLogarAvisoSemExcecao() throws Exception {
        when(s3Client.headBucket(any(HeadBucketRequest.class)))
                .thenThrow(new RuntimeException("Conexão recusada"));

        bucketInitializer.run(null);

        verify(s3Client, never()).createBucket(any(CreateBucketRequest.class));
    }
}
