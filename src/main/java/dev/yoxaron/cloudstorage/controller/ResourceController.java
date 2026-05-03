package dev.yoxaron.cloudstorage.controller;

import dev.yoxaron.cloudstorage.dto.ParsedPath;
import dev.yoxaron.cloudstorage.dto.ResourceResponseDto;
import dev.yoxaron.cloudstorage.exception.InvalidSearchQueryException;
import dev.yoxaron.cloudstorage.security.SecurityUser;
import dev.yoxaron.cloudstorage.service.ResourceMetadataService;
import dev.yoxaron.cloudstorage.service.StorageService;
import dev.yoxaron.cloudstorage.utils.PathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final StorageService storageService;
    private final ResourceMetadataService resourceMetadataService;

    @GetMapping
    public ResponseEntity<ResourceResponseDto> getResourceInfo(
            @RequestParam("path") String path,
            @AuthenticationPrincipal SecurityUser user
    ) {
        ParsedPath parsedPath = PathUtil.validateAndParse(path);

        return ResponseEntity.ok()
                .body(resourceMetadataService.getResourceInfo(parsedPath.path(), parsedPath.name(), user.getId()));
    }

    @PostMapping
    public ResponseEntity<List<ResourceResponseDto>> uploadResources(
            @RequestParam("path") String path,
            @RequestParam("object") List<MultipartFile> files,
            @AuthenticationPrincipal SecurityUser user
    ) {
        PathUtil.validate(path);

        List<ResourceResponseDto> uploadedFiles = storageService.uploadAll(path, files, user.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(uploadedFiles);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteResource(
            @RequestParam("path") String path,
            @AuthenticationPrincipal SecurityUser user
    ) {
        storageService.deleteResource(path, user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResourceResponseDto>> search(
            @RequestParam("query") String query,
            @AuthenticationPrincipal SecurityUser user
    ) {
        if (query.isBlank()) {
            throw new InvalidSearchQueryException("Search query must not be empty");
        }

        return ResponseEntity.ok(resourceMetadataService.search(query, user.getId()));
    }
}
