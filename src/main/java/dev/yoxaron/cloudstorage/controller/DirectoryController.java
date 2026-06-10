package dev.yoxaron.cloudstorage.controller;

import dev.yoxaron.cloudstorage.docs.directory.CreateDirectoryDocs;
import dev.yoxaron.cloudstorage.docs.directory.GetDirectoryDocs;
import dev.yoxaron.cloudstorage.dto.ResourceResponseDto;
import dev.yoxaron.cloudstorage.security.SecurityUser;
import dev.yoxaron.cloudstorage.service.ResourceMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/directory")
@RequiredArgsConstructor
public class DirectoryController {

    private final ResourceMetadataService resourceMetadataService;

    @GetMapping
    @GetDirectoryDocs
    public ResponseEntity<List<ResourceResponseDto>> getDirectory(
            @RequestParam("path") String path,
            @AuthenticationPrincipal SecurityUser user
    ) {
        return ResponseEntity.ok(resourceMetadataService.getDirectoryContents(path, user.getId()));
    }

    @PostMapping
    @CreateDirectoryDocs
    public ResponseEntity<ResourceResponseDto> createDirectory(
            @RequestParam("path") String path,
            @AuthenticationPrincipal SecurityUser user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resourceMetadataService.createDirectory(path, user.getId()));

    }
}
