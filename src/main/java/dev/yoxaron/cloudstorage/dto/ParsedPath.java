package dev.yoxaron.cloudstorage.dto;

public record ParsedPath(
        String path,
        String name,
        boolean isDirectory
) {
}
