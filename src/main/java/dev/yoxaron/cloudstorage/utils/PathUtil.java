package dev.yoxaron.cloudstorage.utils;

import dev.yoxaron.cloudstorage.dto.ParsedPath;
import dev.yoxaron.cloudstorage.exception.InvalidPathException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class PathUtil {

    private static final Pattern DANGEROUS_SEGMENTS = Pattern.compile("(^|/)\\.\\.?(/|$)");

    public static ParsedPath validateAndParse(String path) {
        validate(path);
        return parse(path);
    }

    public static void validate(String path) {
        if (path == null || path.isBlank()) {
            throw new InvalidPathException("Path cannot be empty");
        }
        if (!path.startsWith("/") && path.endsWith("/")) {
            throw new InvalidPathException("Path must start with '/'");
        }
        if (path.contains("//")) {
            throw new InvalidPathException("Path contains double slash");
        }
        if (path.contains("\0")) {
            throw new InvalidPathException("Path contains null byte");
        }
        if (DANGEROUS_SEGMENTS.matcher(path).find()) {
            throw new InvalidPathException("Path traversal detected");
        }
    }

    //todo проверить что в имени файла нет /

    public static ParsedPath parse(String path) {
        if ("/".equals(path)) {
            return new ParsedPath("/", "/", true);
        }

        boolean isDirectory = path.endsWith("/");

        String normalizedPath = isDirectory ? path.substring(0, path.length() - 1) : path;
        int lastSlashIdx = normalizedPath.lastIndexOf("/");
        String parentPath = normalizedPath.substring(0, lastSlashIdx + 1);
        String name = normalizedPath.substring(lastSlashIdx + 1);

        return new ParsedPath(parentPath, name, isDirectory);
    }

    public static Set<String> extractUniqueFilePaths(String path, List<MultipartFile> files) {
        Set<String> uniquePaths = new HashSet<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename(); //todo может вернуть null
            String fileRelativePath = PathUtil.validateAndParse(fileName).path(); //todo имя файла начинается не с /
            String fileAbsolutePath = path + fileRelativePath;
            uniquePaths.add(fileAbsolutePath);
        }
        return uniquePaths;
    }

    public static List<ParsedPath> validateAndParseUniquePaths(Set<String> paths) {
        List<ParsedPath> parsedPaths = new ArrayList<>();
        for (String path : paths) {
            ParsedPath parsedPath = validateAndParse(path);
            parsedPaths.add(parsedPath);//todo вызывается второй раз за метод
        }
        return parsedPaths;
    }
}
