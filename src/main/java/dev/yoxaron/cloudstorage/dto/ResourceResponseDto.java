package dev.yoxaron.cloudstorage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResourceResponseDto(
        String path,
        String name,
        long size,
        String type
) {
}
