package ua.com.alevel.exceptions;

public class SelfAccessModificationException extends RuntimeException {
    public SelfAccessModificationException(String message) {
        super(message);
    }
}
