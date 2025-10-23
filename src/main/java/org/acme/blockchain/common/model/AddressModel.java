package org.acme.blockchain.common.model;

public record AddressModel(String value) {

    public static final String PREFIX = "COPO_";

    public AddressModel {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid address: " + value);
        }
    }

    @Override
    public String toString() {
        return this.value.substring(0, 21);
    }

    private boolean isValid(String value) {
        return value != null && value.matches("^" + PREFIX + "[a-f0-9]{64}$");
    }
}
