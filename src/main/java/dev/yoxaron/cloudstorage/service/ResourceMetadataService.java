package dev.yoxaron.cloudstorage.service;

import dev.yoxaron.cloudstorage.dto.ParsedPath;
import dev.yoxaron.cloudstorage.dto.ResourceResponseDto;
import dev.yoxaron.cloudstorage.entity.Resource;
import dev.yoxaron.cloudstorage.entity.ResourceStatus;
import dev.yoxaron.cloudstorage.entity.ResourceType;
import dev.yoxaron.cloudstorage.exception.ResourceAlreadyExistsException;
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

    public Resource getResource(String path, String name, Long userId) {
        Optional<Resource> maybeResource =
                resourceRepository.findResourceByPathAndNameAndUserId(path, name, userId);

        if (maybeResource.isEmpty()) {
            throw new ResourceNotFoundException("Resource not found");
        }

        return maybeResource.get();
    }

    public ResourceResponseDto getResourceInfo(String path, String name, Long userId) {
        return resourceMapper.toResourceDto(getResource(path, name, userId));
    }

    public List<Resource> getAllFilesByPrefix(String prefix, Long userId) {
        List<Resource> resources =
                resourceRepository.findAllByPathStartingWithAndTypeAndUserId(prefix, ResourceType.FILE, userId);

        if (resources.isEmpty()) {
            throw new ResourceNotFoundException("Folder is empty");
        }

        return resources;
    }

    public List<ResourceResponseDto> getDirectoryContents(String prefix, Long userId) {
        return resourceRepository.findAllByPathAndUserId(prefix, userId).stream()
                .map(resourceMapper::toResourceDto)
                .toList();
    }

    @Transactional
    public ResourceResponseDto createDirectory(ParsedPath parsedPath, Long userId) {
        Optional<Resource> maybeResource =
                resourceRepository.findResourceByPathAndNameAndUserId(parsedPath.path(), parsedPath.name(), userId);

        if (maybeResource.isPresent()) {
            throw new ResourceAlreadyExistsException("Directory already exists");
        }

        Resource directoryToSave = Resource.builder()
                .path(parsedPath.path())
                .name(parsedPath.name())
                .userId(userId)
                .type(ResourceType.DIRECTORY)
                .build();

        return resourceMapper.toResourceDto(resourceRepository.save(directoryToSave));
    }

    @Transactional
    public List<Resource> upsertAllDirectories(List<ParsedPath> parsedPaths, Long userId) {
        List<Resource> createdDirectories = new ArrayList<>();

        for (ParsedPath parsedPath : parsedPaths) {
            Optional<Resource> maybeDirectory =
                    resourceRepository.findResourceByPathAndNameAndUserId(parsedPath.path(), parsedPath.name(), userId);

            if (maybeDirectory.isEmpty()) {
                Resource directoryToSave = Resource.builder()
                        .path(parsedPath.path())
                        .name(parsedPath.name())
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

    public List<ResourceResponseDto> search(String query, Long userId) {
        return resourceRepository.findAllByQueryAndUserId(query, userId).stream()
                .map(resourceMapper::toResourceDto)
                .toList();
    }

    @Transactional
    public ResourceResponseDto moveOrRenameFile(ParsedPath parsedPathFrom, ParsedPath parsedPathTo, Long userId) {
        Resource fileToUpdate = resourceRepository.findResourceByPathAndNameAndTypeAndUserId(
                        parsedPathFrom.path(), parsedPathFrom.name(), ResourceType.FILE, userId)
                .orElseThrow(() -> new InvalidPathException("Invalid path, resource does not exist"));

        ParsedPath destinationDir = parse(parsedPathTo.path());
        boolean destinationDirExists = resourceRepository.existsByPathAndNameAndTypeAndUserId(
                destinationDir.path(), destinationDir.name(), ResourceType.DIRECTORY, userId);

        if (!destinationDirExists) {
            throw new InvalidPathException("Destination directory does not exist");
        }

        boolean destinationFileExists = resourceRepository.existsByPathAndNameAndTypeAndUserId(
                parsedPathTo.path(), parsedPathTo.name(), ResourceType.FILE, userId);

        if (destinationFileExists) {
            throw new ResourceAlreadyExistsException("Cannot update resource, " +
                    "resource with such path and name already exists");
        }

        fileToUpdate.setPath(parsedPathTo.path());
        fileToUpdate.setName(parsedPathTo.name());

        return resourceMapper.toResourceDto(fileToUpdate);
    }

    @Transactional
    public ResourceResponseDto moveOrRenameDirectory(ParsedPath parsedPathFrom,
                                                     ParsedPath parsedPathTo, Long userId) {

        Resource dirToUpdate = resourceRepository.findResourceByPathAndNameAndTypeAndUserId(
                        parsedPathFrom.path(), parsedPathFrom.name(), ResourceType.DIRECTORY, userId)
                .orElseThrow(() -> new InvalidPathException("Invalid path, resource does not exist"));

        boolean destDirExists = resourceRepository.existsByPathAndNameAndTypeAndUserId(
                parsedPathTo.path(), parsedPathTo.name(), ResourceType.DIRECTORY, userId);

        if (destDirExists) {
            throw new ResourceAlreadyExistsException("Cannot update directory, such directory already exists");
        }

        boolean isSameDir = parsedPathFrom.path().equals(parsedPathTo.path());
        boolean isSameName = parsedPathFrom.name().equals(parsedPathTo.name());

//        if (isSameDir == isSameName) {
//            throw new InvalidPathException("Move and rename cannot be performed simultaneously");
//        }

        dirToUpdate.setName(parsedPathTo.name());
        dirToUpdate.setPath(parsedPathTo.path());

        resourceRepository.updateNestedPaths(getPrefix(parsedPathFrom), getPrefix(parsedPathTo), userId);

        return resourceMapper.toResourceDto(dirToUpdate);
    }

    public boolean isDirectoryExists(String path, Long userId) {
        ParsedPath parsedPath = validateAndParseDirectory(path);

        return resourceRepository.existsByPathAndNameAndTypeAndUserId(
                parsedPath.path(), parsedPath.name(), ResourceType.DIRECTORY, userId);
    }
}
