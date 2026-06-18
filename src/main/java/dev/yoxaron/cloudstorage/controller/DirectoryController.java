package dev.yoxaron.cloudstorage.controller;

import dev.yoxaron.cloudstorage.api.DirectoryApi;
import dev.yoxaron.cloudstorage.dto.ResourceResponseDto;
import dev.yoxaron.cloudstorage.security.SecurityUser;
import dev.yoxaron.cloudstorage.service.ResourceMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DirectoryController implements DirectoryApi {

    private final ResourceMetadataService resourceMetadataService;

    @Override
    public ResponseEntity<List<ResourceResponseDto>> getDirectory(String path, SecurityUser user) {
        return ResponseEntity.ok(resourceMetadataService.getDirectoryContents(path, user.getId()));
    }

    @Override
    public ResponseEntity<ResourceResponseDto> createDirectory(String path, SecurityUser user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resourceMetadataService.createDirectory(path, user.getId()));

    }
}
