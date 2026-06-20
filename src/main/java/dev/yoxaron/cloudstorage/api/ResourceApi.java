package dev.yoxaron.cloudstorage.api;

import dev.yoxaron.cloudstorage.docs.common.CommonErrorResponses;
import dev.yoxaron.cloudstorage.docs.common.CommonResourceErrorResponses;
import dev.yoxaron.cloudstorage.docs.resource.*;
import dev.yoxaron.cloudstorage.dto.request.MoveRequestDto;
import dev.yoxaron.cloudstorage.dto.request.ResourcePathRequestDto;
import dev.yoxaron.cloudstorage.dto.request.SearchRequestDto;
import dev.yoxaron.cloudstorage.dto.request.UploadRequestDto;
import dev.yoxaron.cloudstorage.dto.response.ResourceResponseDto;
import dev.yoxaron.cloudstorage.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RequestMapping("/api/resource")
public interface ResourceApi {

    @Operation(
            summary = "Get resource information",
            description = """
                Returns information about a file or directory.
                
                The path must be URL-encoded.
                
                Paths to directories must start and end with / to distinguish them
                from files with the same name.
                """,
            parameters = {
                    @Parameter(
                            name = "path",
                            description = """
                                Full URL-encoded path to the resource.
                                
                                Path to directory must start and end with /.
                                
                                Path to file must start with /.
                                """,
                            required = true,
                            example = "/folder1/folder2/file.txt"
                    )
            }
    )
    @GetMapping
    @GetResourceInfoDocs
    @CommonResourceErrorResponses
    @CommonErrorResponses
    ResponseEntity<ResourceResponseDto> getResourceInfo(
            @Valid @ModelAttribute ResourcePathRequestDto pathRequest,
            @AuthenticationPrincipal SecurityUser user
    );

    @Operation(
            summary = "Upload resources",
            description = """
                Uploads one or more files into an existing directory.

                Files are sent using multipart/form-data in the "object" field.

                If an uploaded file contains relative path information in its original filename,
                the directory structure is recreated automatically.

                For example, uploading a file named:

                photos/2025/image.jpg

                to:

                /storage/

                will result in:

                /storage/photos/2025/image.jpg
               
                Directories /storage/photos/ and /storage/photos/2025/ are created automatically

                This allows uploading entire directories and nested directory structures
                in a single request.

                The destination directory specified by the path parameter must already exist.
                
                Max file size: 50MB
                Max request size: 50MB
                Max file count: 100
                """,
            parameters = {
                    @Parameter(
                            name = "path",
                            description = """
                                Full URL-encoded path to the destination directory.
                                The directory must already exist.
                                """,
                            required = true,
                            example = "/storage/"
                    )
            },
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE
                    )
            )
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @UploadResourceDocs
    @CommonErrorResponses
    ResponseEntity<List<ResourceResponseDto>> uploadResources(
            @Valid @ModelAttribute UploadRequestDto uploadRequest,
            @AuthenticationPrincipal SecurityUser user
    );

    @Operation(
            summary = "Delete resource",
            description = """
                Deletes a file or directory.
                
                The path must be URL-encoded.
                
                Paths to directories must start and end with / to distinguish them
                from files with the same name.
                """,
            parameters = {
                    @Parameter(
                            name = "path",
                            description = """
                                Full URL-encoded path to the resource.
                                
                                Path to directory must start and end with /.
                                
                                Path to file must start with /.
                                """,
                            required = true,
                            example = "/folder1/folder2/file.txt"
                    )
            }
    )
    @DeleteMapping
    @DeleteResourceDocs
    @CommonResourceErrorResponses
    @CommonErrorResponses
    ResponseEntity<Void> deleteResource(
            @Valid @ModelAttribute ResourcePathRequestDto pathRequest,
            @AuthenticationPrincipal SecurityUser user
    );

    @Operation(
            summary = "Search resources",
            description = """
                Searches for resources by name.

                The search query must be URL-encoded.

                The response contains all matching files and directories for current user.
                """,
            parameters = {
                    @Parameter(
                            name = "query",
                            description = "URL-encoded search query",
                            required = true,
                            example = "report"
                    )
            }
    )
    @GetMapping("/search")
    @SearchResourcesDocs
    @CommonErrorResponses
    ResponseEntity<List<ResourceResponseDto>> search(
            @Valid @ModelAttribute SearchRequestDto searchRequest,
            @AuthenticationPrincipal SecurityUser user
    );

    @Operation(
            summary = "Download resource",
            description = """
                Downloads a file or directory.
                
                The path must be URL-encoded.
                
                Paths to directories must end with / to distinguish them
                from files with the same name.
                
                Files are downloaded as-is.
                
                Directories are downloaded as ZIP archives containing their contents.
                """,
            parameters = {
                    @Parameter(
                            name = "path",
                            description = """
                                Full URL-encoded path to the resource.
                                
                                Paths to directories must start and end with /.
                                
                                Paths to file must start with /.
                                """,
                            required = true,
                            example = "/folder1/folder2/file.txt"
                    )
            }
    )
    @GetMapping("/download")
    @DownloadResourceDocs
    @CommonResourceErrorResponses
    @CommonErrorResponses
    ResponseEntity<StreamingResponseBody> download(
            @Valid @ModelAttribute ResourcePathRequestDto pathRequest,
            @AuthenticationPrincipal SecurityUser user
    );

    @Operation(
            summary = "Move or rename resource",
            description = """
                Moves or renames a file or directory.

                Both source and destination paths must be URL-encoded.

                Paths to directories must end with /.

                Source and destination paths must refer to the same resource type.

                A file can only be moved or renamed as a file.
                A directory can only be moved or renamed as a directory.

                Renaming is performed by changing only the resource name while keeping
                the same parent directory.

                Example:

                from: /documents/report.pdf
                to:   /documents/final-report.pdf

                Moving is performed by changing only the parent directory while keeping
                the same resource name.

                Example:

                from: /documents/report.pdf
                to:   /archive/report.pdf

                The response contains information about the resource after the operation.
                """,
            parameters = {
                    @Parameter(
                            name = "from",
                            description = "Current URL-encoded path of the resource",
                            required = true,
                            example = "/documents/report.pdf"
                    ),
                    @Parameter(
                            name = "to",
                            description = "New URL-encoded path of the resource",
                            required = true,
                            example = "/archive/report.pdf"
                    )
            }
    )
    @GetMapping("/move")
    @MoveResourceDocs
    @CommonErrorResponses
    ResponseEntity<ResourceResponseDto> move(
            @Valid @ModelAttribute MoveRequestDto moveRequest,
            @AuthenticationPrincipal SecurityUser user
    );
}
