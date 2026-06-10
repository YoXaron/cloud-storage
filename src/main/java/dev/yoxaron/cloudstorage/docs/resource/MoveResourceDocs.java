package dev.yoxaron.cloudstorage.docs.resource;

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
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Resource moved or renamed successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ResourceResponseDto.class
                        ),
                        examples = {
                                @ExampleObject(
                                        name = "File",
                                        value = """
                                                {
                                                  "path": "/archive/",
                                                  "name": "report.pdf",
                                                  "size": 123,
                                                  "type": "FILE"
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "Directory",
                                        value = """
                                                {
                                                  "path": "/documents/",
                                                  "name": "archive",
                                                  "type": "DIRECTORY"
                                                }
                                                """
                                )
                        }
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid source or destination path",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ErrorResponseDto.class
                        ),
                        examples = @ExampleObject(
                                value = """
                                        {
                                          "message": "Paths must both be either directories or files"
                                        }
                                        """
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
                description = "Resource or destination directory not found",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ErrorResponseDto.class
                        ),
                        examples = {
                                @ExampleObject(
                                        name = "Resource not found",
                                        value = """
                                                {
                                                  "message": "Resource not found"
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "Destination directory not found",
                                        value = """
                                                {
                                                  "message": "Destination directory not found"
                                                }
                                                """
                                )
                        }
                )
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Destination resource already exists",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ErrorResponseDto.class
                        ),
                        examples = @ExampleObject(
                                value = """
                                        {
                                          "message": "Destination resource already exists"
                                        }
                                        """
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
public @interface MoveResourceDocs {
}
