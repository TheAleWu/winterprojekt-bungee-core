package de.alewu.wpbc.files;

public class FileHandleException extends RuntimeException {

    public FileHandleException(String message) {
        super(message);
    }

    public FileHandleException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileHandleException(Throwable cause) {
        super(cause);
    }

    public FileHandleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
