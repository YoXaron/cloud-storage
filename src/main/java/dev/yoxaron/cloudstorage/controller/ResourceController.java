package dev.yoxaron.cloudstorage.controller;

import dev.yoxaron.cloudstorage.dto.ParsedPath;
import dev.yoxaron.cloudstorage.dto.ResourceResponseDto;
import dev.yoxaron.cloudstorage.security.SecurityUser;
import dev.yoxaron.cloudstorage.service.ResourceService;
import dev.yoxaron.cloudstorage.utils.PathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping
    public ResponseEntity<ResourceResponseDto> getResourceInfo(@RequestParam("path") String path,
                                                               @AuthenticationPrincipal SecurityUser user) {
        ParsedPath parsedPath = PathUtil.validateAndParse(path);

        return ResponseEntity.ok()
                .body(resourceService.getResourceInfo(parsedPath.path(), parsedPath.name(), user.getId()));
    }
}
