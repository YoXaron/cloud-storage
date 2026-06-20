package dev.yoxaron.cloudstorage.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SearchRequestDto(
        @NotBlank(message = "Search query must not be empty") String query
) {
}
