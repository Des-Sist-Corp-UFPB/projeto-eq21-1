package br.ufpb.dsc.mercado.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;

@Component
public class BucketInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BucketInitializer.class);

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public BucketInitializer(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            criarBucketSeNecessario();
            aplicarPoliticaPublica();
        } catch (Exception e) {
            log.warn("Não foi possível configurar o bucket MinIO: {}", e.getMessage());
        }
    }

    private void criarBucketSeNecessario() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
            log.info("Bucket '{}' criado.", bucket);
        }
    }

    private void aplicarPoliticaPublica() {
        String policy = """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": {"AWS": ["*"]},
                      "Action": ["s3:GetObject"],
                      "Resource": ["arn:aws:s3:::%s/*"]
                    }
                  ]
                }
                """.formatted(bucket);

        s3Client.putBucketPolicy(PutBucketPolicyRequest.builder()
                .bucket(bucket)
                .policy(policy)
                .build());
        log.info("Política pública de leitura aplicada no bucket '{}'.", bucket);
    }
}
