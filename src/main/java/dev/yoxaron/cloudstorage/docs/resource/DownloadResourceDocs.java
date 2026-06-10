package dev.yoxaron.cloudstorage.docs.resource;

import dev.yoxaron.cloudstorage.docs.SwaggerExamples;
import dev.yoxaron.cloudstorage.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
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
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Resource downloaded successfully",
                content = @Content(
                        mediaType = "application/octet-stream"
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid or missing path",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ErrorResponseDto.class
                        ),
                        examples = @ExampleObject(
                                value = SwaggerExamples.INVALID_PATH
                        )
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "User is not authenticated",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ErrorResponseDto.class
                        ),
                        examples = @ExampleObject(
                                value = SwaggerExamples.NOT_AUTHENTICATED
                        )
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Resource not found",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ErrorResponseDto.class
                        ),
                        examples = @ExampleObject(
                                value = SwaggerExamples.RESOURCE_NOT_FOUND
                        )
                )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ErrorResponseDto.class
                        ),
                        examples = @ExampleObject(
                                value = SwaggerExamples.INTERNAL_ERROR
                        )
                )
        )
})
public @interface DownloadResourceDocs {
}
