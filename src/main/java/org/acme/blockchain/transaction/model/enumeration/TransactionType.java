package org.acme.blockchain.transaction.model.enumeration;

import lombok.Getter;

@Getter
public enum TransactionType {

    TRANSFER("TX"),

    REWARD("RW");

    private final String type;

    TransactionType(String type) {
        this.type = type;
    }
}
