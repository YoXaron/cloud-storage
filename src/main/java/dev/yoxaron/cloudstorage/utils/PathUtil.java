package dev.yoxaron.cloudstorage.utils;

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

    public static ParsedPath validateAndParseDirectory(String path) {
        ParsedPath parsedPath = validateAndParse(path);

        if (!parsedPath.isDirectory()) {
            throw new InvalidPathException("Path must end with /");
        }

        return parsedPath;
    }

    public static void validate(String path) {
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
        Set<String> uniqueFilePaths = new HashSet<>();

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();

            if (fileName == null || fileName.isBlank()) {
                throw new InvalidPathException("File has no original filename");
            }

            String fileRelativePath = PathUtil.validateAndParse(fileName).path();
            uniqueFilePaths.addAll(extractUniquePaths(path + fileRelativePath));
        }

        return uniqueFilePaths;
    }

    public static Set<String> extractUniquePaths(String path) {
        Set<String> uniquePaths = new HashSet<>();
        uniquePaths.add("/");

        String[] segments = path.split("/");
        StringBuilder currentPath = new StringBuilder("/");

        for (String segment : segments) {
            if (segment.isBlank()) continue;
            currentPath.append(segment).append("/");
            uniquePaths.add(currentPath.toString());
        }

        String curr = currentPath.toString();
        if (!curr.endsWith("/")) {
            uniquePaths.remove(curr);
        }

        return uniquePaths;
    }

    public static List<ParsedPath> validateAndParseUniquePaths(Set<String> paths) {
        List<ParsedPath> parsedPaths = new ArrayList<>();
        for (String path : paths) {
            ParsedPath parsedPath = validateAndParse(path);
            parsedPaths.add(parsedPath);
        }
        return parsedPaths;
    }

    public static String getPrefix(ParsedPath parsedPath) {
        if (parsedPath.path().equals("/") && parsedPath.name().equals("/")) {
            return "/";
        }
        return parsedPath.path() + parsedPath.name() + "/";
    }

    public static boolean isRootDir(ParsedPath parsedPath) {
        return parsedPath.isDirectory() && "/".equals(parsedPath.path()) && "/".equals(parsedPath.name());
    }
}
