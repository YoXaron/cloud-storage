package dev.yoxaron.cloudstorage.docs.resource;

import dev.yoxaron.cloudstorage.docs.SwaggerExamples;
import dev.yoxaron.cloudstorage.dto.ErrorResponseDto;
import dev.yoxaron.cloudstorage.dto.ResourceResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
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
                
                Max file size: 20MB
                Max request size: 50MB
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
                                value = """
                                        {
                                          "message": "Uploading failed"
                                        }
                                        """
                        )
                )
        )
})
public @interface UploadResourceDocs {
}
