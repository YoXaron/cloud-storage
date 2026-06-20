package dev.yoxaron.cloudstorage.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MoveRequestDto(
        @NotBlank(message = "From path must not be empty") String from,
        @NotBlank(message = "To path must not be empty") String to
) {
}
