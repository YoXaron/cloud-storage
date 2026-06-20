package dev.yoxaron.cloudstorage.docs.resource;

import dev.yoxaron.cloudstorage.dto.response.ErrorResponseDto;
import dev.yoxaron.cloudstorage.dto.response.ResourceResponseDto;
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
@ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Resources uploaded successfully",
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
                                            "path": "/storage/",
                                            "name": "file.txt",
                                            "size": 123,
                                            "type": "FILE"
                                          },
                                          {
                                            "path": "/storage/photos/2025/",
                                            "name": "image.jpg",
                                            "size": 456,
                                            "type": "FILE"
                                          }
                                        ]
                                        """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid path, missing destination directory or malformed upload request",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ErrorResponseDto.class
                        ),
                        examples = @ExampleObject(
                                value = """
                                        {
                                          "message": "Destination directory does not exist"
                                        }
                                        """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "409",
                description = "A resource with the same path already exists",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ErrorResponseDto.class
                        ),
                        examples = @ExampleObject(
                                value = """
                                        {
                                          "message": "Resource already exists: image.jpg"
                                        }
                                        """
                        )
                )
        )
})
public @interface UploadResourceDocs {
}
