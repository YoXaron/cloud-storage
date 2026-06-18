package dev.yoxaron.cloudstorage.api;

import dev.yoxaron.cloudstorage.docs.directory.CreateDirectoryDocs;
import dev.yoxaron.cloudstorage.docs.directory.GetDirectoryDocs;
import dev.yoxaron.cloudstorage.dto.ResourceResponseDto;
import dev.yoxaron.cloudstorage.security.SecurityUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/api/directory")
public interface DirectoryApi {

    @GetMapping
    @GetDirectoryDocs
    ResponseEntity<List<ResourceResponseDto>> getDirectory(
            @RequestParam("path") String path,
            @AuthenticationPrincipal SecurityUser user
    );

    @PostMapping
    @CreateDirectoryDocs
    ResponseEntity<ResourceResponseDto> createDirectory(
            @RequestParam("path") String path,
            @AuthenticationPrincipal SecurityUser user
    );
}
