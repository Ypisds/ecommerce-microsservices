package com.ypisds.ecommerce_microsservices.produto_service.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinIOService {
    
    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    public String uploadFile(String bucketName, MultipartFile file) throws IOException {
        String url = UUID.randomUUID() + "_" + file.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                                .bucket(bucketName)
                                .key(url)
                                .contentType(file.getContentType())
                                .build();
        try (InputStream inputStream = file.getInputStream()) {
            this.s3Client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
        }

        return url;
    }

    public String createPresignedGetUrl(String bucketName, String url) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(url)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))  // The URL will expire in 15 minutes.
                .getObjectRequest(objectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        log.info("Presigned URL: [{}]", presignedRequest.url().toString());
        log.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

        return presignedRequest.url().toExternalForm(); 
    }

    public void createBucketIfNotExists(String bucketName) {
    try {
        // Verifica se o bucket já existe
        s3Client.headBucket(HeadBucketRequest.builder()
                .bucket(bucketName)
                .build());
        log.info("Bucket '{}' já existe.", bucketName);
    } catch (NoSuchBucketException e) {
        // Se não existir, cria
        s3Client.createBucket(CreateBucketRequest.builder()
                .bucket(bucketName)
                .build());
        log.info("Bucket '{}' criado com sucesso.", bucketName);
    }
}
}
