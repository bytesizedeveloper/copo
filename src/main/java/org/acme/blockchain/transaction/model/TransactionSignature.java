package org.acme.blockchain.transaction.model;

import org.acme.blockchain.common.utility.HashUtility;

public record TransactionSignature(String value) {

    private static final String REWARD = "REWARD";

    public static final TransactionSignature REWARD_SIGNATURE = new TransactionSignature(REWARD);

    public TransactionSignature {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid signature: " + value);
        }
    }

    public byte[] toBytes() {
        return HashUtility.hexToBytes(this.value);
    }

    private boolean isValid(String value) {
        return value != null
                && (value.equals(REWARD)
                || value.matches("^[a-f0-9]{9254}$"));
    }
}
