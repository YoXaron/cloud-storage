package dev.yoxaron.cloudstorage.docs.directory;

import dev.yoxaron.cloudstorage.docs.SwaggerExamples;
import dev.yoxaron.cloudstorage.dto.ErrorResponseDto;
import dev.yoxaron.cloudstorage.dto.ResourceResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Directory contents retrieved successfully",
                content = @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(
                                schema = @Schema(
                                        implementation = ResourceResponseDto.class
                                )
                        ),
                        examples = @ExampleObject(
                                value = """
                                        [
                                          {
                                            "path": "/folder1/folder2/",
                                            "name": "file.txt",
                                            "size": 123,
                                            "type": "FILE"
                                          },
                                          {
                                            "path": "/folder1/folder2/",
                                            "name": "documents",
                                            "type": "DIRECTORY"
                                          }
                                        ]
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
                description = "Directory not found",
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
public @interface GetDirectoryDocs {
}
