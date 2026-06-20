package dev.yoxaron.cloudstorage.docs.user;

import dev.yoxaron.cloudstorage.docs.SwaggerExamples;
import dev.yoxaron.cloudstorage.dto.response.UserAuthResponseDto;
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
                description = "Current user retrieved successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = UserAuthResponseDto.class
                        ),
                        examples = @ExampleObject(
                                value = SwaggerExamples.USER_RESPONSE
                        )
                )
        )
})
public @interface GetCurrentUserDocs {
}
