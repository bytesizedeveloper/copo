package org.acme.blockchain.transaction.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.acme.blockchain.common.model.Address;
import org.acme.blockchain.common.model.Coin;
import org.acme.blockchain.common.utility.HashUtility;
import org.acme.blockchain.transaction.model.enumeration.TransactionStatus;
import org.acme.blockchain.transaction.model.enumeration.TransactionType;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
@EqualsAndHashCode
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(force = true)
public abstract class TransactionModel {

    protected TransactionHash hashId;

    protected Address senderAddress;

    protected final Address recipientAddress;

    @Setter
    protected byte[] senderPublicKeyEncoded;

    protected final Coin amount;

    @Setter
    protected Coin fee;

    protected TransactionType type;

    @Setter
    protected OffsetDateTime createdAt;

    @Setter
    protected List<UtxoModel> inputs;

    @Setter
    protected List<UtxoModel> outputs;

    @Setter
    protected TransactionSignature signature;

    @Setter
    protected TransactionStatus status;

    protected final List<String> inputIds;

    public void calculateHashId() {
        String hashId = HashUtility.calculateSHA256d(getData());
        this.hashId = new TransactionHash(hashId);
    }

    public String getData() {
        return this.senderAddress.value() +
                this.recipientAddress.value() +
                Arrays.toString(this.senderPublicKeyEncoded) +
                this.amount.value() +
                this.fee.value() +
                this.type +
                this.createdAt +
                Arrays.toString(this.getInputIds());
    }

    public boolean isTransfer() {
        return TransactionType.TRANSFER.equals(this.type);
    }

    public String[] getInputIds() {
        return this.inputs != null ? this.inputs.stream()
                .map(input -> input.getId().toString())
                .toArray(String[]::new) : new String[0];
    }

    public abstract void generateOutputs();
}
