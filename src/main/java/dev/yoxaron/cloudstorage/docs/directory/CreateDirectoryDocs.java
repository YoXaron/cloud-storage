package dev.yoxaron.cloudstorage.docs.directory;

import dev.yoxaron.cloudstorage.docs.SwaggerExamples;
import dev.yoxaron.cloudstorage.dto.ErrorResponseDto;
import dev.yoxaron.cloudstorage.dto.ResourceResponseDto;
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
@ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Directory created successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ResourceResponseDto.class
                        ),
                        examples = @ExampleObject(
                                value = """
                                        {
                                          "path": "/folder1/folder2/folder3/",
                                          "name": "folder4",
                                          "type": "DIRECTORY"
                                        }
                                        """
                        )
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
                responseCode = "409",
                description = "Directory already exists",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ErrorResponseDto.class
                        ),
                        examples = @ExampleObject(
                                value = SwaggerExamples.RESOURCE_ALREADY_EXISTS
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
public @interface CreateDirectoryDocs {
}
