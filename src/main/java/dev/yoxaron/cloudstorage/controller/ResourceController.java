package dev.yoxaron.cloudstorage.controller;

import dev.yoxaron.cloudstorage.api.ResourceApi;
import dev.yoxaron.cloudstorage.dto.internal.DownloadResult;
import dev.yoxaron.cloudstorage.dto.request.MoveRequestDto;
import dev.yoxaron.cloudstorage.dto.request.ResourcePathRequestDto;
import dev.yoxaron.cloudstorage.dto.request.SearchRequestDto;
import dev.yoxaron.cloudstorage.dto.request.UploadRequestDto;
import dev.yoxaron.cloudstorage.dto.response.ResourceResponseDto;
import dev.yoxaron.cloudstorage.security.SecurityUser;
import dev.yoxaron.cloudstorage.service.ResourceMetadataService;
import dev.yoxaron.cloudstorage.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResourceController implements ResourceApi {

    private final StorageService storageService;
    private final ResourceMetadataService resourceMetadataService;

    @Override
    public ResponseEntity<ResourceResponseDto> getResourceInfo(ResourcePathRequestDto pathRequest,
                                                               SecurityUser user) {
        return ResponseEntity.ok()
                .body(resourceMetadataService.getResourceInfo(pathRequest.path(), user.getId()));
    }

    @Override
    public ResponseEntity<List<ResourceResponseDto>> uploadResources(UploadRequestDto uploadRequest,
                                                                     SecurityUser user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storageService.uploadAll(uploadRequest.path(), uploadRequest.object(), user.getId()));
    }

    @Override
    public ResponseEntity<Void> deleteResource(ResourcePathRequestDto pathRequest, SecurityUser user) {
        storageService.deleteResource(pathRequest.path(), user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<List<ResourceResponseDto>> search(SearchRequestDto searchRequest, SecurityUser user) {
        return ResponseEntity.ok(resourceMetadataService.search(searchRequest.query(), user.getId()));
    }

    @Override
    public ResponseEntity<StreamingResponseBody> download(ResourcePathRequestDto pathRequest, SecurityUser user) {
        DownloadResult downloadResult = storageService.download(pathRequest.path(), user.getId());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, downloadResult.attachmentName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(downloadResult.body());
    }

    @Override
    public ResponseEntity<ResourceResponseDto> move(MoveRequestDto moveRequest, SecurityUser user) {
        return ResponseEntity.ok()
                .body(storageService.moveOrRename(moveRequest.from(), moveRequest.to(), user.getId()));
    }
}
