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
                responseCode = "200",
                description = "Resources found successfully",
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
                                            "path": "/documents/",
                                            "name": "report.pdf",
                                            "size": 123,
                                            "type": "FILE"
                                          },
                                          {
                                            "path": "/",
                                            "name": "reports",
                                            "type": "DIRECTORY"
                                          }
                                        ]
                                        """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid or missing search query",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ErrorResponseDto.class
                        ),
                        examples = @ExampleObject(
                                value = """
                                        {
                                          "message": "Search query must not be empty"
                                        }
                                        """
                        )
                )
        )
})
public @interface SearchResourcesDocs {
}
