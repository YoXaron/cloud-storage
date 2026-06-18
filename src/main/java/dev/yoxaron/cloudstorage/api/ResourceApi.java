package dev.yoxaron.cloudstorage.api;

import dev.yoxaron.cloudstorage.docs.resource.*;
import dev.yoxaron.cloudstorage.dto.ResourceResponseDto;
import dev.yoxaron.cloudstorage.security.SecurityUser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RequestMapping("/api/resource")
public interface ResourceApi {

    @GetMapping
    @GetResourceInfoDocs
    ResponseEntity<ResourceResponseDto> getResourceInfo(
            @RequestParam("path") String path,
            @AuthenticationPrincipal SecurityUser user
    );

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @UploadResourceDocs
    ResponseEntity<List<ResourceResponseDto>> uploadResources(
            @RequestParam("path") String path,
            @RequestParam("object") List<MultipartFile> files,
            @AuthenticationPrincipal SecurityUser user
    );

    @DeleteMapping
    @DeleteResourceDocs
    ResponseEntity<Void> deleteResource(
            @RequestParam("path") String path,
            @AuthenticationPrincipal SecurityUser user
    );

    @GetMapping("/search")
    @SearchResourcesDocs
    ResponseEntity<List<ResourceResponseDto>> search(
            @RequestParam("query") String query,
            @AuthenticationPrincipal SecurityUser user
    );

    @GetMapping("/download")
    @DownloadResourceDocs
    ResponseEntity<StreamingResponseBody> download(
            @RequestParam("path") String path,
            @AuthenticationPrincipal SecurityUser user
    );

    @GetMapping("/move")
    @MoveResourceDocs
    ResponseEntity<ResourceResponseDto> move(
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @AuthenticationPrincipal SecurityUser user
    );
}
