package org.acme.blockchain.transaction.model.enumeration;

import lombok.Getter;

import java.util.List;

@Getter
public enum TransactionStatus {

    INITIALISED("INI"),

    VALIDATED("VAL"),

    INVALIDATED("INV"),

    BROADCASTED("BRD"),

    CONFIRMED("CFM"),

    REJECTED("REJ"),

    MINED("MND"),

    FAILED("FAI");

    private static final List<TransactionStatus> TERMINAL;

    static {
        TERMINAL = List.of(INVALIDATED, REJECTED, MINED, FAILED);
    }

    private final String status;

    TransactionStatus(String status) {
        this.status = status;
    }

    public static List<TransactionStatus> getTerminal() {
        return TERMINAL;
    }
}
