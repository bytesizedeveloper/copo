package org.acme.blockchain.transaction.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.acme.blockchain.transaction.model.enumeration.OutputIndex;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class UtxoId {

    private static final String SEPARATOR = ":";

    private TransactionHash transactionHashId;

    private OutputIndex outputIndex;

    public UtxoId(String id) {
        String[] elements = id.split(SEPARATOR);
        this.transactionHashId = new TransactionHash(elements[0]);
        this.outputIndex = OutputIndex.fromIndex(elements[1]);
    }

    @Override
    public String toString() {
        return this.transactionHashId + SEPARATOR + outputIndex;
    }
}
