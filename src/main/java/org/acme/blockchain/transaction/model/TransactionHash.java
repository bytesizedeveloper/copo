package org.acme.blockchain.transaction.model;

public record TransactionHash(String value) {

    public TransactionHash {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid transaction hash: " + value);
        }
    }

    private boolean isValid(String value) {
        return value != null && value.matches("^[a-f0-9]{64}$");
    }
}
