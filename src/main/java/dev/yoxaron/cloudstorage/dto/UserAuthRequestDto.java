package dev.yoxaron.cloudstorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User registration request")
public record UserAuthRequestDto(

        @Schema(
                description = "Unique username",
                example = "user_1"
        )
        @NotBlank
        @Size(min = 4, max = 50)
        String username,

        @Schema(
                description = "User password",
                example = "password"
        )
        @NotBlank
        @Size(min = 8, max = 255)
        String password
) {
}
