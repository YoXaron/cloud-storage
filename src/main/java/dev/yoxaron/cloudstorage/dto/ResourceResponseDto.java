package dev.yoxaron.cloudstorage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "File system resource")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResourceResponseDto(

        @Schema(
                description = "Path to the parent directory",
                example = "/folder1/folder2/"
        )
        String path,

        @Schema(
                description = "Resource name",
                example = "file.txt"
        )
        String name,

        @Schema(
                description = "File size in bytes. Absent for directories",
                example = "123"
        )
        Long size,

        @Schema(
                description = "Resource type",
                allowableValues = {"FILE", "DIRECTORY"},
                example = "FILE"
        )
        String type
) {
}
