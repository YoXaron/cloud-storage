package dev.yoxaron.cloudstorage.api;

import dev.yoxaron.cloudstorage.docs.common.CommonErrorResponses;
import dev.yoxaron.cloudstorage.docs.common.CommonResourceErrorResponses;
import dev.yoxaron.cloudstorage.docs.directory.CreateDirectoryDocs;
import dev.yoxaron.cloudstorage.docs.directory.GetDirectoryDocs;
import dev.yoxaron.cloudstorage.dto.request.ResourcePathRequestDto;
import dev.yoxaron.cloudstorage.dto.response.ResourceResponseDto;
import dev.yoxaron.cloudstorage.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(
            summary = "Get directory contents",
            description = """
                Returns a list of resources located directly in the specified directory.
                
                The listing is not recursive.
                """,
            parameters = {
                    @Parameter(
                            name = "path",
                            description = "Full URL-encoded path to the directory. Must start and end with '/'",
                            required = true,
                            example = "/folder1/folder2/"
                    )
            }
    )
    @GetMapping
    @GetDirectoryDocs
    @CommonResourceErrorResponses
    @CommonErrorResponses
    ResponseEntity<List<ResourceResponseDto>> getDirectory(
            @Valid @ModelAttribute ResourcePathRequestDto pathRequest,
            @AuthenticationPrincipal SecurityUser user
    );

    @Operation(
            summary = "Create directory",
            description = """
                Creates a directory at the specified path.
                
                Missing parent directories are created automatically.
                
                For example, if only the root directory exists and the path is:
                
                /folder1/folder2/folder3/folder4/
                
                then the following directory hierarchy will be created.
            
                The response contains information only about the target directory (folder4).
                """,
            parameters = {
                    @Parameter(
                            name = "path",
                            description = """
                                Full URL-encoded path of the directory to create.
                                Must start and end with '/'.
                                """,
                            required = true,
                            example = "/folder1/folder2/folder3/folder4/"
                    )
            }
    )
    @PostMapping
    @CreateDirectoryDocs
    @CommonResourceErrorResponses
    @CommonErrorResponses
    ResponseEntity<ResourceResponseDto> createDirectory(
            @Valid @ModelAttribute ResourcePathRequestDto pathRequest,
            @AuthenticationPrincipal SecurityUser user
    );
}
