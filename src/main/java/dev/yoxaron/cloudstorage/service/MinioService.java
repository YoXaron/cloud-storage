package dev.yoxaron.cloudstorage.service;

import dev.yoxaron.cloudstorage.config.properties.MinioProperties;
import dev.yoxaron.cloudstorage.exception.MinioException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private static final String USER_FILES_PREFIX = "user-%d-files/";

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public void upload(MultipartFile file, UUID uuid, Long userId) {
        try {
            String objectName = USER_FILES_PREFIX.formatted(userId) + uuid.toString();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.bucketName())
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            log.info("File {} uploaded successfully for user {}", uuid, userId);
        } catch (Exception e) {
            log.error("Failed to upload file {} for user {}", uuid, userId, e);
            throw new MinioException("Failed to upload to minio: " + e.getMessage());
        }
    }

    public InputStream getObjectAsStream(UUID uuid, Long userId) {
        try {
            String objectName = USER_FILES_PREFIX.formatted(userId) + uuid.toString();

            log.debug("Getting object {} for user {}", uuid, userId);
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.bucketName())
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("failed to receive InputStream from minio");
            throw new MinioException("Failed to receive InputStream from MinIO: " + e.getMessage());
        }
    }

    public void deleteObject(UUID uuid, Long userId) {
        try {
            String objectName = USER_FILES_PREFIX.formatted(userId) + uuid.toString();

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.bucketName())
                            .object(objectName)
                            .build()
            );
            log.info("Object {} deleted from MinIO for user {}", uuid, userId);
        } catch (Exception e) {
            log.error("Failed to delete object with UUID {}", uuid, e);
            throw new MinioException("Failed to delete object: " + e.getMessage());
        }
    }
}
