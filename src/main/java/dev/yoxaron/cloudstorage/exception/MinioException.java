package dev.yoxaron.cloudstorage.exception;

public class MinioException extends RuntimeException {
    public MinioException(String message) {
        super(message);
    }
}
