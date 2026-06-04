package dev.yoxaron.cloudstorage.service;

import dev.yoxaron.cloudstorage.dto.ParsedPath;
import dev.yoxaron.cloudstorage.dto.ResourceResponseDto;
import dev.yoxaron.cloudstorage.entity.Resource;
import dev.yoxaron.cloudstorage.entity.ResourceStatus;
import dev.yoxaron.cloudstorage.entity.ResourceType;
import dev.yoxaron.cloudstorage.exception.InvalidPathException;
import dev.yoxaron.cloudstorage.exception.InvalidSearchQueryException;
import dev.yoxaron.cloudstorage.exception.ResourceAlreadyExistsException;
import dev.yoxaron.cloudstorage.exception.ResourceNotFoundException;
import dev.yoxaron.cloudstorage.mapper.ResourceMapper;
import dev.yoxaron.cloudstorage.repository.ResourceRepository;
import dev.yoxaron.cloudstorage.utils.PathUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static dev.yoxaron.cloudstorage.utils.PathUtil.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceMetadataService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    public Resource getResource(String path, String name, ResourceType type, Long userId) {
        if (type.equals(ResourceType.FILE)) {
            return resourceRepository
                    .findResourceByPathAndNameAndTypeAndStatusAndUserId(path, name, type, ResourceStatus.READY, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("File not found"));
        } else {
            return resourceRepository.findResourceByPathAndNameAndTypeAndUserId(path, name, type, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Directory not found"));
        }
    }

    public ResourceResponseDto getResourceInfo(String path, Long userId) {
        ParsedPath parsedPath = PathUtil.validateAndParse(path);
        ResourceType type = parsedPath.isDirectory() ? ResourceType.DIRECTORY : ResourceType.FILE;

        return resourceMapper.toResourceDto(getResource(parsedPath.path(), parsedPath.name(), type, userId));
    }

    public List<Resource> getAllFilesByPrefix(String prefix, Long userId) {
        List<Resource> resources =
                resourceRepository.findAllByPathStartingWithAndTypeAndUserId(prefix, ResourceType.FILE, userId);

        if (resources.isEmpty()) {
            throw new ResourceNotFoundException("Folder is empty");
        }

        return resources;
    }

    public boolean isFileExists(String path, String name, ResourceStatus status, Long userId) {
        return resourceRepository.existsByPathAndNameAndTypeAndStatusAndUserId(
                path, name, ResourceType.FILE, status, userId);
    }

    public List<ResourceResponseDto> getDirectoryContents(String path, Long userId) {
        ParsedPath parsedPath = validateAndParseDirectory(path);
        boolean dirExists = resourceRepository.existsByPathAndNameAndTypeAndUserId(
                parsedPath.path(), parsedPath.name(), ResourceType.DIRECTORY, userId);

        if (!dirExists) {
            throw new ResourceNotFoundException("Directory does not exist");
        }

        String prefix = getPrefix(parsedPath);

        return resourceRepository.getDirectoryContents(prefix, ResourceStatus.READY, userId).stream()
                .map(resourceMapper::toResourceDto)
                .toList();
    }


    @Transactional
    public ResourceResponseDto createDirectory(String path, Long userId) {
        ParsedPath parsedPath = validateAndParseDirectory(path);

        if (parsedPath.path().equals("/") && parsedPath.name().equals("/")) {
            throw new ResourceAlreadyExistsException("Root directory already exists");
        }

        boolean isDirExists = resourceRepository.existsByPathAndNameAndTypeAndUserId(
                parsedPath.path(), parsedPath.name(), ResourceType.DIRECTORY, userId);

        if (isDirExists) {
            throw new ResourceAlreadyExistsException("Directory already exists");
        }

        if (!isRootDir(parsedPath)) {
            upsertParentDirectories(parsedPath.path(), userId);
        }

        Resource directoryToSave = Resource.builder()
                .path(parsedPath.path())
                .name(parsedPath.name())
                .userId(userId)
                .type(ResourceType.DIRECTORY)
                .build();

        return resourceMapper.toResourceDto(resourceRepository.save(directoryToSave));
    }

    private void upsertParentDirectories(String path, Long userId) {
        Set<String> uniqueParentPaths = extractUniquePaths(path);
        validateAndParseUniquePaths(uniqueParentPaths).stream()
                .map(p -> Resource.builder()
                        .path(p.path())
                        .name(p.name())
                        .userId(userId)
                        .type(ResourceType.DIRECTORY)
                        .build())
                .filter(r -> !resourceRepository.existsByPathAndNameAndTypeAndUserId(
                        r.getPath(), r.getName(), ResourceType.DIRECTORY, userId))
                .forEach(resourceRepository::save);
    }

    @Transactional
    public List<Resource> upsertAllDirectories(List<ParsedPath> parsedPaths, Long userId) {
        List<Resource> createdDirectories = new ArrayList<>();

        for (ParsedPath parsedPath : parsedPaths) {
            boolean dirExist = resourceRepository.existsByPathAndNameAndTypeAndUserId(
                    parsedPath.path(), parsedPath.name(), ResourceType.DIRECTORY, userId);

            if (!dirExist) {
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
    public void markFileAsDeleted(ParsedPath parsedPath, Long userId) {
        Optional<Resource> maybeResource = resourceRepository.findResourceByPathAndNameAndTypeAndStatusAndUserId(
                parsedPath.path(), parsedPath.name(), ResourceType.FILE, ResourceStatus.READY, userId);

        if (maybeResource.isPresent()) {
            Resource resource = maybeResource.get();
            resource.setStatus(ResourceStatus.DELETED);
        } else {
            throw new ResourceNotFoundException(
                    "Resource with path %s and name %s not found".formatted(parsedPath.path(), parsedPath.name()));
        }
    }

    @Transactional
    public void deleteDirectory(ParsedPath parsedPath, String prefix, Long userId) {
        boolean dirExists = resourceRepository.existsByPathAndNameAndTypeAndUserId(
                parsedPath.path(), parsedPath.name(), ResourceType.DIRECTORY, userId);

        if (!dirExists) {
            throw new ResourceNotFoundException("Directory does not exist");
        }

        resourceRepository.deleteDirectory(parsedPath.path(), parsedPath.name(), ResourceType.DIRECTORY, userId);
        resourceRepository.deleteNestedDirectories(prefix, ResourceType.DIRECTORY, userId);
        resourceRepository.markAllFilesAsDeleted(prefix, ResourceStatus.DELETED, ResourceType.FILE, userId);
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

        Resource savedResource = resourceRepository.saveAndFlush(resourceToSave);
        return resourceMapper.toResourceDto(savedResource);
    }

    @Transactional
    public void updateStatuses(List<UUID> uuids, ResourceStatus status, Long userId) {
        resourceRepository.updateStatuses(uuids, status, userId);
    }

    public List<ResourceResponseDto> search(String query, Long userId) {
        if (query.isBlank()) {
            throw new InvalidSearchQueryException("Search query must not be empty");
        }

        return resourceRepository.findAllByQueryAndUserId(query, ResourceStatus.READY, userId).stream()
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
