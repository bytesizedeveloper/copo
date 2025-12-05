package org.acme.blockchain.common.model;

public record Address(String value) {

    public static final String PREFIX = "COPO_";

    public Address {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid address: " + value);
        }
    }

    private boolean isValid(String value) {
        return value != null && value.matches("^" + PREFIX + "[a-f0-9]{64}$");
    }
}
