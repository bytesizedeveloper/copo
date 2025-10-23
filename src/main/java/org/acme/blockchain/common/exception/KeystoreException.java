package org.acme.blockchain.common.exception;

public class KeystoreException extends RuntimeException {

    public KeystoreException(String message) {
        super(message);
    }

    public KeystoreException(String message, Exception e) {
        super(message, e);
    }
}
