package dev.yoxaron.cloudstorage.docs.directory;

import dev.yoxaron.cloudstorage.docs.SwaggerExamples;
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
        )
})
public @interface CreateDirectoryDocs {
}
