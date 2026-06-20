package dev.yoxaron.cloudstorage.utils;

public record ParsedPath(
        String path,
        String name,
        boolean isDirectory
) {
}
