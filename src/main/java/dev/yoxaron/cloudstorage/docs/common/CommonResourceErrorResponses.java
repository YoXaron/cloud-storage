package dev.yoxaron.cloudstorage.docs.common;

import dev.yoxaron.cloudstorage.docs.SwaggerExamples;
import dev.yoxaron.cloudstorage.dto.response.ErrorResponseDto;
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
        )
})
public @interface CommonResourceErrorResponses {
}
