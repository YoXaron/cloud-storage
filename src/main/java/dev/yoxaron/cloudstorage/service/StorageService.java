package dev.yoxaron.cloudstorage.service;

import dev.yoxaron.cloudstorage.dto.internal.DownloadResult;
import dev.yoxaron.cloudstorage.dto.response.ResourceResponseDto;
import dev.yoxaron.cloudstorage.entity.Resource;
import dev.yoxaron.cloudstorage.entity.ResourceStatus;
import dev.yoxaron.cloudstorage.entity.ResourceType;
import dev.yoxaron.cloudstorage.exception.InvalidPathException;
import dev.yoxaron.cloudstorage.exception.ResourceAlreadyExistsException;
import dev.yoxaron.cloudstorage.exception.UploadingFailedException;
import dev.yoxaron.cloudstorage.utils.ParsedPath;
import dev.yoxaron.cloudstorage.utils.PathUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static dev.yoxaron.cloudstorage.utils.PathUtil.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final ResourceMetadataService resourceMetadataService;
    private final MinioService minioService;

    public List<ResourceResponseDto> uploadAll(String path, List<MultipartFile> files, Long userId) {
        validate(path);

        if (!resourceMetadataService.isDirectoryExists(path, userId)) {
            throw new InvalidPathException("Destination directory does not exist");
        }

        log.info("User {} uploading {} files to {}", userId, files.size(), path);
        List<Resource> createdDirectories = createNewRelativeDirectories(path, files, userId);

        List<UUID> uploadingUUIDs = new ArrayList<>();
        List<ResourceResponseDto> createdResourceDtos = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                ParsedPath parsedPath = parse(path + file.getOriginalFilename());

                boolean fileExists = resourceMetadataService.isFileExists(
                        parsedPath.path(), parsedPath.name(), ResourceStatus.READY, userId);

                if (fileExists) {
                    throw new ResourceAlreadyExistsException("Resource already exists: " + parsedPath.name());
                }

                UUID uuid = UUID.randomUUID();
                uploadingUUIDs.add(uuid);

                ResourceResponseDto createdResourceDto =
                        resourceMetadataService.saveUploadingMetadata(parsedPath, uuid, file.getSize(), userId);

                minioService.upload(file, uuid, userId);
                createdResourceDtos.add(createdResourceDto);
            }
        } catch (ResourceAlreadyExistsException e) {
            log.warn("Upload cancelled, resource already exists for user {}: {}", userId, e.getMessage());
            rollback(uploadingUUIDs, createdDirectories, userId);
            throw e;
        } catch (Exception e) {
            log.error("Upload failed for user {} to path {}", userId, path, e);
            rollback(uploadingUUIDs, createdDirectories, userId);
            throw new UploadingFailedException("Uploading failed: " + e.getMessage());
        }

        resourceMetadataService.updateStatuses(uploadingUUIDs, ResourceStatus.READY, userId);
        log.info("User {} successfully uploaded {} files to {}", userId, files.size(), path);
        return createdResourceDtos;
    }

    private void rollback(List<UUID> uploadingUUIDs, List<Resource> createdDirectories, Long userId) {
        resourceMetadataService.updateStatuses(uploadingUUIDs, ResourceStatus.FAILED, userId);
        resourceMetadataService.deleteAllResources(createdDirectories);
        log.debug("Roll back, set status FAILED for {} files, deleted {} directories for user {}",
                uploadingUUIDs.size(), createdDirectories.size(), userId);
    }

    public DownloadResult download(String path, Long userId) {
        ParsedPath parsedPath = validateAndParse(path);
        log.debug("User {} downloading {}", userId, path);
        if (parsedPath.isDirectory()) {
            StreamingResponseBody zipStreamBody = getZipAsStream(parsedPath, userId);
            return new DownloadResult(
                    "attachment; filename=\"" + parsedPath.name() + ".zip\"", zipStreamBody);
        } else {
            StreamingResponseBody streamBody = getFileAsStream(parsedPath, userId)::transferTo;
            return new DownloadResult(
                    "attachment; filename=\"" + parsedPath.name() + "\"", streamBody);
        }
    }

    private InputStream getFileAsStream(ParsedPath parsedPath, Long userId) {
        Resource resource =
                resourceMetadataService.getResource(parsedPath.path(), parsedPath.name(), ResourceType.FILE, userId);
        return minioService.getObjectAsStream(resource.getUuid(), userId);
    }

    private StreamingResponseBody getZipAsStream(ParsedPath parsedPath, Long userId) {
        String prefix = getPrefix(parsedPath);
        List<Resource> resources = resourceMetadataService.getAllFilesByPrefix(prefix, userId);

        return outputStream -> {
            try (ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(outputStream))) {
                for (Resource resource : resources) {
                    InputStream inputStream = minioService.getObjectAsStream(resource.getUuid(), userId);
                    String resourceName = resource.getPath() + resource.getName();
                    String entryName = resourceName.substring(prefix.length());
                    zipOut.putNextEntry(new ZipEntry(entryName));
                    StreamUtils.copy(inputStream, zipOut);
                    zipOut.closeEntry();
                }
            }
        };
    }

    public void deleteResource(String path, Long userId) {
        ParsedPath parsedPath = validateAndParse(path);

        if (isRootDir(parsedPath)) {
            throw new InvalidPathException("It is forbidden to delete the root directory");
        }

        if (parsedPath.isDirectory()) {
            String prefix = getPrefix(parsedPath);
            resourceMetadataService.deleteDirectory(parsedPath, prefix, userId);
        } else {
            resourceMetadataService.markFileAsDeleted(parsedPath, userId);
        }
        log.info("Resource {} deleted for user {}", path, userId);
    }

    public ResourceResponseDto moveOrRename(String fromPath, String toPath, Long userId) {
        ParsedPath parsedPathFrom = PathUtil.validateAndParse(fromPath);
        ParsedPath parsedPathTo = PathUtil.validateAndParse(toPath);

        if (parsedPathFrom.isDirectory() != parsedPathTo.isDirectory()) {
            throw new InvalidPathException("Paths must both be either directories or files");
        }

        if (parsedPathFrom.isDirectory()) {
            return resourceMetadataService.moveOrRenameDirectory(parsedPathFrom, parsedPathTo, userId);
        } else {
            return resourceMetadataService.moveOrRenameFile(parsedPathFrom, parsedPathTo, userId);
        }
    }

    private List<Resource> createNewRelativeDirectories(String path, List<MultipartFile> files, Long userId) {
        List<ParsedPath> parsedPaths = extractAndValidateUniquePaths(path, files);
        return resourceMetadataService.upsertAllDirectories(parsedPaths, userId);
    }

    private List<ParsedPath> extractAndValidateUniquePaths(String path, List<MultipartFile> files) {
        Set<String> uniquePaths = extractUniqueFilePaths(path, files);
        return validateAndParseUniquePaths(uniquePaths);
    }
}
