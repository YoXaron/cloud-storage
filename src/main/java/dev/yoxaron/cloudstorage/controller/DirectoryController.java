package dev.yoxaron.cloudstorage.controller;

import dev.yoxaron.cloudstorage.dto.ParsedPath;
import dev.yoxaron.cloudstorage.dto.ResourceResponseDto;
import dev.yoxaron.cloudstorage.exception.ResourceAlreadyExistsException;
import dev.yoxaron.cloudstorage.security.SecurityUser;
import dev.yoxaron.cloudstorage.service.ResourceMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static dev.yoxaron.cloudstorage.utils.PathUtil.*;

@RestController
@RequestMapping("/api/directory")
@RequiredArgsConstructor
public class DirectoryController {

    private final ResourceMetadataService resourceMetadataService;

    @GetMapping
    public ResponseEntity<List<ResourceResponseDto>> getDirectory(
            @RequestParam("path") String path,
            @AuthenticationPrincipal SecurityUser user
    ) {
        ParsedPath parsedPath = validateAndParse(path);
        String prefix = getPrefix(parsedPath);

        List<ResourceResponseDto> directoryContents =
                resourceMetadataService.getDirectoryContents(prefix, user.getId());

        return ResponseEntity.ok(directoryContents);
    }

    @PostMapping
    public ResponseEntity<ResourceResponseDto> createDirectory(
            @RequestParam("path") String path,
            @AuthenticationPrincipal SecurityUser user
    ) {
        ParsedPath parsedPath = validateAndParseDirectory(path);

        if (parsedPath.path().equals("/") && parsedPath.name().equals("/")) {
            throw new ResourceAlreadyExistsException("Root directory already exists");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resourceMetadataService.createDirectory(parsedPath, user.getId()));

    }
}
