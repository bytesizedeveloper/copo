package org.acme.blockchain.block.model;

public record BlockHash(String value) {

    public static final BlockHash GENESIS_PREVIOUS_HASH = new BlockHash("0000000000000000000000000000000000000000000000000000000000000000");

    public BlockHash {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid block hash: " + value);
        }
    }

    public boolean startsWithLeadingZeroes(int difficulty) {
        return this.value.startsWith("0".repeat(difficulty));
    }

    private boolean isValid(String value) {
        return value != null && value.matches("^[a-f0-9]{64}$");
    }
}
