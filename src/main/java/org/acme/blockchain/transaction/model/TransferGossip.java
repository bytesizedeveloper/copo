package org.acme.blockchain.transaction.model;

import lombok.Data;

@Data
public class TransferGossip {

    private int confirmations = 0;

    private int rejections = 0;

    public int confirm() {
        return this.confirmations++;
    }

    public int reject() {
        return this.rejections++;
    }
}
