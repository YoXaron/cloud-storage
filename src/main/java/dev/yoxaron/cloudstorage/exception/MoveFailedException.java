package dev.yoxaron.cloudstorage.exception;

public class MoveFailedException extends RuntimeException {
    public MoveFailedException(String message) {
        super(message);
    }
}
