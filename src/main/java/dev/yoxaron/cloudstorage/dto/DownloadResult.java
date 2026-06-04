package dev.yoxaron.cloudstorage.dto;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public record DownloadResult(String attachmentName, StreamingResponseBody body) {
}
