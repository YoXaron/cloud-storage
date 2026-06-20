package dev.yoxaron.cloudstorage.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ResourcePathRequestDto(
        @NotBlank(message = "Path must not be empty") String path
) {
}
