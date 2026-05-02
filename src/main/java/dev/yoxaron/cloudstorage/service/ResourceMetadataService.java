package dev.yoxaron.cloudstorage.service;

import dev.yoxaron.cloudstorage.dto.ParsedPath;
import dev.yoxaron.cloudstorage.dto.ResourceResponseDto;
import dev.yoxaron.cloudstorage.entity.Resource;
import dev.yoxaron.cloudstorage.entity.ResourceStatus;
import dev.yoxaron.cloudstorage.entity.ResourceType;
import dev.yoxaron.cloudstorage.exception.ResourceNotFoundException;
import dev.yoxaron.cloudstorage.mapper.ResourceMapper;
import dev.yoxaron.cloudstorage.repository.ResourceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceMetadataService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    public ResourceResponseDto getResourceInfo(String path, String name, Long userId) {
        Optional<Resource> maybeResource =
                resourceRepository.findResourceByPathAndNameAndUserId(path, name, userId);

        if (maybeResource.isEmpty()) {
            throw new ResourceNotFoundException("Resource not found");
        }

        return resourceMapper.toResourceDto(maybeResource.get());
    }

    @Transactional
    public List<Resource> upsertAllDirectories(List<ParsedPath> paths, Long userId) {
        //todo N+1
        List<Resource> createdDirectories = new ArrayList<>();
        for (ParsedPath path : paths) {
            Optional<Resource> maybeDirectory =
                    resourceRepository.findResourceByPathAndNameAndUserId(path.path(), path.name(), userId);

            if (maybeDirectory.isEmpty()) {
                Resource directoryToSave = Resource.builder()
                        .path(path.path())
                        .name(path.name())
                        .userId(userId)
                        .type(ResourceType.DIRECTORY)
                        .build();

                createdDirectories.add(resourceRepository.save(directoryToSave));
            }
        }
        return createdDirectories;
    }

    @Transactional
    public void deleteAllResources(List<Resource> resourcesToDelete) {
        resourceRepository.deleteAll(resourcesToDelete);
    }

    @Transactional
    public void markAsDeleted(ParsedPath parsedPath, Long userId) {
        Optional<Resource> maybeResource = resourceRepository.findResourceByPathAndNameAndUserId(
                parsedPath.path(), parsedPath.name(), userId);

        if (maybeResource.isPresent()) {
            Resource resource = maybeResource.get();
            resourceRepository.updateStatus(resource.getUuid(), ResourceStatus.DELETED, userId);
        } else {
            throw new ResourceNotFoundException(
                    "Resource with path %s and name %s not found".formatted(parsedPath.path(), parsedPath.name()));
        }
    }

    @Transactional
    public void deleteDirectory(ParsedPath parsedPath, String prefix, Long userId) {
        resourceRepository.deleteDirectory(parsedPath.path(), parsedPath.name(), userId);
        resourceRepository.deleteNestedDirectories(prefix, userId);
        resourceRepository.markAllFilesAsDeleted(prefix, userId);
    }

    @Transactional
    public ResourceResponseDto saveUploadingMetadata(ParsedPath parsedPath, UUID uuid, long size, Long userId) {
        Resource resourceToSave = Resource.builder()
                .path(parsedPath.path())
                .name(parsedPath.name())
                .userId(userId)
                .type(ResourceType.FILE)
                .status(ResourceStatus.UPLOADING)
                .size(size)
                .uuid(uuid)
                .build();

        Resource savedResource = resourceRepository.save(resourceToSave);
        return resourceMapper.toResourceDto(savedResource);
    }

    @Transactional
    public void updateStatuses(List<UUID> uuids, ResourceStatus status, Long userId) {
        resourceRepository.updateStatuses(uuids, status, userId);
    }


    @Transactional
    public void markAsFailed(List<UUID> uuids, Long userId) {
        resourceRepository.updateStatuses(uuids, ResourceStatus.FAILED, userId);
    }
}
