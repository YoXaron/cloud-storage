package dev.yoxaron.cloudstorage.api;

import dev.yoxaron.cloudstorage.docs.directory.CreateDirectoryDocs;
import dev.yoxaron.cloudstorage.docs.directory.GetDirectoryDocs;
import dev.yoxaron.cloudstorage.dto.request.ResourcePathRequestDto;
import dev.yoxaron.cloudstorage.dto.response.ResourceResponseDto;
import dev.yoxaron.cloudstorage.security.SecurityUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/directory")
public interface DirectoryApi {

    @GetMapping
    @GetDirectoryDocs
    ResponseEntity<List<ResourceResponseDto>> getDirectory(
            @Valid @ModelAttribute ResourcePathRequestDto pathRequest,
            @AuthenticationPrincipal SecurityUser user
    );

    @PostMapping
    @CreateDirectoryDocs
    ResponseEntity<ResourceResponseDto> createDirectory(
            @Valid @ModelAttribute ResourcePathRequestDto pathRequest,
            @AuthenticationPrincipal SecurityUser user
    );
}
