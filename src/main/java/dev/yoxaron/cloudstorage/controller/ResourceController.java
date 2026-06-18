package dev.yoxaron.cloudstorage.controller;

import dev.yoxaron.cloudstorage.api.ResourceApi;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResourceController implements ResourceApi {

    private final StorageService storageService;
    private final ResourceMetadataService resourceMetadataService;

    @Override
    public ResponseEntity<ResourceResponseDto> getResourceInfo(String path, SecurityUser user) {
        return ResponseEntity.ok()
                .body(resourceMetadataService.getResourceInfo(path, user.getId()));
    }

    @Override
    public ResponseEntity<List<ResourceResponseDto>> uploadResources(
            String path,
            List<MultipartFile> files,
            SecurityUser user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storageService.uploadAll(path, files, user.getId()));
    }

    @Override
    public ResponseEntity<Void> deleteResource(String path, SecurityUser user) {
        storageService.deleteResource(path, user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<List<ResourceResponseDto>> search(String query, SecurityUser user) {
        return ResponseEntity.ok(resourceMetadataService.search(query, user.getId()));
    }

    @Override
    public ResponseEntity<StreamingResponseBody> download(String path, SecurityUser user) {
        DownloadResult downloadResult = storageService.download(path, user.getId());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, downloadResult.attachmentName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(downloadResult.body());
    }

    @Override
    public ResponseEntity<ResourceResponseDto> move(String from, String to, SecurityUser user) {
        return ResponseEntity.ok()
                .body(storageService.moveOrRename(from, to, user.getId()));
    }
}
