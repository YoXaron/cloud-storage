package dev.yoxaron.cloudstorage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UploadRequestDto(
        @NotBlank(message = "Path must not be empty") String path,
        @NotEmpty(message = "Files must not be empty") List<MultipartFile> object
) {
}
