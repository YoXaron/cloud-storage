package dev.yoxaron.cloudstorage.docs.resource;

import dev.yoxaron.cloudstorage.dto.response.ErrorResponseDto;
import dev.yoxaron.cloudstorage.dto.response.ResourceResponseDto;
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
        )
})
public @interface MoveResourceDocs {
}
