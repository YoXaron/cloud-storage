package dev.yoxaron.cloudstorage.dto.internal;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public record DownloadResult(String attachmentName, StreamingResponseBody body) {
}
