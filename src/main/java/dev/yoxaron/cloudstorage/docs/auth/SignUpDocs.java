package dev.yoxaron.cloudstorage.docs.auth;

import dev.yoxaron.cloudstorage.docs.SwaggerExamples;
import dev.yoxaron.cloudstorage.dto.ErrorResponseDto;
import dev.yoxaron.cloudstorage.dto.UserAuthResponseDto;
import io.swagger.v3.oas.annotations.Operation;
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
        summary = "Register user",
        description = """
                Creates a new user account.
                
                After successful registration, the user is automatically authenticated
                and a session cookie is created.
                """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "User registered successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = UserAuthResponseDto.class
                        ),
                        examples = @ExampleObject(
                                value = SwaggerExamples.USER_RESPONSE
                        )
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ErrorResponseDto.class
                        ),
                        examples = @ExampleObject(
                                value = SwaggerExamples.AUTH_VALIDATION_ERROR
                        )
                )
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Username already exists",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = ErrorResponseDto.class
                        ),
                        examples = @ExampleObject(
                                value = SwaggerExamples.USER_ALREADY_EXISTS
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
public @interface SignUpDocs {
}
