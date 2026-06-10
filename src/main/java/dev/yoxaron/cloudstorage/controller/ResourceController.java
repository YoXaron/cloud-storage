package dev.yoxaron.cloudstorage.controller;

import dev.yoxaron.cloudstorage.docs.resource.*;
import dev.yoxaron.cloudstorage.dto.DownloadResult;
import dev.yoxaron.cloudstorage.dto.ResourceResponseDto;
import dev.yoxaron.cloudstorage.security.SecurityUser;
import dev.yoxaron.cloudstorage.service.ResourceMetadataService;
import dev.yoxaron.cloudstorage.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final StorageService storageService;
    private final ResourceMetadataService resourceMetadataService;

    @GetMapping
    @GetResourceInfoDocs
    public ResponseEntity<ResourceResponseDto> getResourceInfo(
            @RequestParam("path") String path,
            @AuthenticationPrincipal SecurityUser user
    ) {
        return ResponseEntity.ok()
                .body(resourceMetadataService.getResourceInfo(path, user.getId()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @UploadResourceDocs
    public ResponseEntity<List<ResourceResponseDto>> uploadResources(
            @RequestParam("path") String path,
            @RequestParam("object") List<MultipartFile> files,
            @AuthenticationPrincipal SecurityUser user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storageService.uploadAll(path, files, user.getId()));
    }

    @DeleteMapping
    @DeleteResourceDocs
    public ResponseEntity<Void> deleteResource(
            @RequestParam("path") String path,
            @AuthenticationPrincipal SecurityUser user
    ) {
        storageService.deleteResource(path, user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/search")
    @SearchResourcesDocs
    public ResponseEntity<List<ResourceResponseDto>> search(
            @RequestParam("query") String query,
            @AuthenticationPrincipal SecurityUser user
    ) {
        return ResponseEntity.ok(resourceMetadataService.search(query, user.getId()));
    }

    @GetMapping("/download")
    @DownloadResourceDocs
    public ResponseEntity<StreamingResponseBody> download(
            @RequestParam("path") String path,
            @AuthenticationPrincipal SecurityUser user
    ) {
        DownloadResult downloadResult = storageService.download(path, user.getId());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, downloadResult.attachmentName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(downloadResult.body());
    }

    @GetMapping("/move")
    @MoveResourceDocs
    public ResponseEntity<ResourceResponseDto> move(
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @AuthenticationPrincipal SecurityUser user
    ) {
        return ResponseEntity.ok()
                .body(storageService.moveOrRename(from, to, user.getId()));
    }
}
