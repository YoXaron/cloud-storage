package dev.yoxaron.cloudstorage.service;

import dev.yoxaron.cloudstorage.entity.Resource;
import dev.yoxaron.cloudstorage.entity.ResourceStatus;
import dev.yoxaron.cloudstorage.repository.ResourceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataCleanupService {

    private final ResourceRepository resourceRepository;
    private final MinioService minioService;

    @Transactional
    public int cleanup(Instant threshold) {
        List<Resource> resourcesToCleanup = resourceRepository.findResourcesToCleanup(
                List.of(ResourceStatus.DELETED, ResourceStatus.FAILED, ResourceStatus.UPLOADING),
                threshold);

        if (resourcesToCleanup.isEmpty()) {
            return 0;
        }

        cleanupMetadata(resourcesToCleanup);
        cleanupMinio(resourcesToCleanup);

        return resourcesToCleanup.size();
    }

    private void cleanupMetadata(List<Resource> resourcesToCleanup) {
        resourceRepository.deleteAll(resourcesToCleanup);
    }

    private void cleanupMinio(List<Resource> resourcesToCleanup) {
        for (Resource resource : resourcesToCleanup) {
            minioService.deleteObject(resource.getUuid(), resource.getUserId());
        }
    }
}
