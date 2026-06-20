package dev.yoxaron.cloudstorage.docs.resource;

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
                description = "Resource information retrieved successfully",
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
                                                  "path": "/folder1/folder2/",
                                                  "name": "file.txt",
                                                  "size": 123,
                                                  "type": "FILE"
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "Directory",
                                        value = """
                                                {
                                                  "path": "/folder1/",
                                                  "name": "folder2",
                                                  "type": "DIRECTORY"
                                                }
                                                """
                                )
                        }
                )
        )
})
public @interface GetResourceInfoDocs {
}
