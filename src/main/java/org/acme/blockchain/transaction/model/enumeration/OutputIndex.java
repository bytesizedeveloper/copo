package org.acme.blockchain.transaction.model.enumeration;

import lombok.Getter;

@Getter
public enum OutputIndex {

    RECIPIENT("00"),

    SENDER("01");

    private final String index;

    OutputIndex(String index) {
        this.index = index;
    }

}
