package dev.yoxaron.cloudstorage.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinioInitializer {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @PostConstruct
    public void init() {
        String bucketName = minioProperties.bucketName();

        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build());

            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("MiniO bucket {} created", bucketName);
            } else {
                log.info("MiniO bucket {} already exists", bucketName);
            }
        } catch (Exception e) {
            log.error("Failed to initialize MiniO");
            throw new IllegalStateException("Error occurred while initializing MiniO", e);
        }
    }
}
