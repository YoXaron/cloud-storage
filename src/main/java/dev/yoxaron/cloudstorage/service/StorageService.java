package dev.yoxaron.cloudstorage.service;

import dev.yoxaron.cloudstorage.dto.ParsedPath;
import dev.yoxaron.cloudstorage.dto.ResourceResponseDto;
import dev.yoxaron.cloudstorage.entity.Resource;
import dev.yoxaron.cloudstorage.entity.ResourceStatus;
import dev.yoxaron.cloudstorage.exception.UploadingFailedException;
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
        List<Resource> createdDirectories = createNewDirectories(path, files, userId);

        List<UUID> uploadingUUIDs = new ArrayList<>();
        List<ResourceResponseDto> createdResourceDtos = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                ParsedPath parsedPath = parse(path + file.getOriginalFilename());

                UUID uuid = UUID.randomUUID();
                uploadingUUIDs.add(uuid);

                ResourceResponseDto createdResourceDto =
                        resourceMetadataService.saveUploadingMetadata(parsedPath, uuid, file.getSize(), userId);

                minioService.upload(file, uuid, userId);
                createdResourceDtos.add(createdResourceDto);
            }
        } catch (Exception e) {
            resourceMetadataService.markAsFailed(uploadingUUIDs, userId);
            resourceMetadataService.deleteAllResources(createdDirectories);
            throw new UploadingFailedException("Uploading failed: " + e.getMessage());
        }

        resourceMetadataService.updateStatuses(uploadingUUIDs, ResourceStatus.READY, userId);
        return createdResourceDtos;
    }

    public InputStream getFileAsStream(ParsedPath parsedPath, Long userId) {
        Resource resource = resourceMetadataService.getResource(parsedPath.path(), parsedPath.name(), userId);
        return minioService.getObjectAsStream(resource.getUuid(), userId);
    }

    public StreamingResponseBody getZipAsStream(ParsedPath parsedPath, Long userId) {
        String prefix = getPrefix(parsedPath);
        List<Resource> resources = resourceMetadataService.getAllFilesByPrefix(prefix, userId);

        return outputStream -> {
            try (ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(outputStream))) {
                for (Resource resource : resources) {
                    InputStream inputStream = minioService.getObjectAsStream(resource.getUuid(), userId);
                    String resourceName = resource.getPath() + resource.getName();
                    String entryName =  resourceName.substring(prefix.length());
                    zipOut.putNextEntry(new ZipEntry(entryName));
                    StreamUtils.copy(inputStream, zipOut);
                    zipOut.closeEntry();
                }
            }
        };
    }

    public void deleteResource(String path, Long userId) {
        ParsedPath parsedPath = validateAndParse(path);
        resourceMetadataService.getResourceInfo(parsedPath.path(), parsedPath.name(), userId);

        String prefix = getPrefix(parsedPath);
        if (parsedPath.isDirectory()) {
            resourceMetadataService.deleteDirectory(parsedPath, prefix, userId);
        } else {
            resourceMetadataService.markAsDeleted(parsedPath, userId);
        }
    }

    public ResourceResponseDto moveOrRename(ParsedPath parsedPathFrom, ParsedPath parsedPathTo, Long userId) {
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
