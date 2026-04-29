package dev.yoxaron.cloudstorage.service;

import dev.yoxaron.cloudstorage.config.MinioProperties;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private static final String USER_FILES_PREFIX = "user-%d-files/";

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public void upload(MultipartFile file, UUID uuid, Long userId) {
        //minio client upload
        try {
            String objectName = USER_FILES_PREFIX.formatted(userId) + uuid.toString();
            log.info("uploading to minio {}", objectName);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.bucketName())
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            log.info("file successfully uploaded");
        } catch (Exception e) {
            log.info("minio uploading error {}", e.getMessage());
            throw new RuntimeException("Failed to upload to minio", e); //todo
        }
    }
}
