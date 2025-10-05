package org.acme.common.exception;

public class CryptographicException extends RuntimeException {

    public CryptographicException(String message) {
        super(message);
    }

    public CryptographicException(String message, Exception e) {
        super(message, e);
    }
}
