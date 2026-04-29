package dev.yoxaron.cloudstorage.exception;

public class UploadingFailedException extends RuntimeException {
    public UploadingFailedException(String message) {
        super(message);
    }
}
