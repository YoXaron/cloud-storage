package dev.yoxaron.cloudstorage.api;

import dev.yoxaron.cloudstorage.docs.resource.*;
import dev.yoxaron.cloudstorage.dto.request.MoveRequestDto;
import dev.yoxaron.cloudstorage.dto.request.ResourcePathRequestDto;
import dev.yoxaron.cloudstorage.dto.request.SearchRequestDto;
import dev.yoxaron.cloudstorage.dto.request.UploadRequestDto;
import dev.yoxaron.cloudstorage.dto.response.ResourceResponseDto;
import dev.yoxaron.cloudstorage.security.SecurityUser;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RequestMapping("/api/resource")
public interface ResourceApi {

    @GetMapping
    @GetResourceInfoDocs
    ResponseEntity<ResourceResponseDto> getResourceInfo(
            @Valid @ModelAttribute ResourcePathRequestDto pathRequest,
            @AuthenticationPrincipal SecurityUser user
    );

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @UploadResourceDocs
    ResponseEntity<List<ResourceResponseDto>> uploadResources(
            @Valid @ModelAttribute UploadRequestDto uploadRequest,
            @AuthenticationPrincipal SecurityUser user
    );

    @DeleteMapping
    @DeleteResourceDocs
    ResponseEntity<Void> deleteResource(
            @Valid @ModelAttribute ResourcePathRequestDto pathRequest,
            @AuthenticationPrincipal SecurityUser user
    );

    @GetMapping("/search")
    @SearchResourcesDocs
    ResponseEntity<List<ResourceResponseDto>> search(
            @Valid @ModelAttribute SearchRequestDto searchRequest,
            @AuthenticationPrincipal SecurityUser user
    );

    @GetMapping("/download")
    @DownloadResourceDocs
    ResponseEntity<StreamingResponseBody> download(
            @Valid @ModelAttribute ResourcePathRequestDto pathRequest,
            @AuthenticationPrincipal SecurityUser user
    );

    @GetMapping("/move")
    @MoveResourceDocs
    ResponseEntity<ResourceResponseDto> move(
            @Valid @ModelAttribute MoveRequestDto moveRequest,
            @AuthenticationPrincipal SecurityUser user
    );
}
