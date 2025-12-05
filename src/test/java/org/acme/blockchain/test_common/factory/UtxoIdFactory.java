package org.acme.blockchain.test_common.factory;

import org.acme.blockchain.transaction.model.UtxoId;
import org.instancio.Instancio;

import static org.instancio.Select.field;

public final class UtxoIdFactory {

    public static UtxoId getUtxoId() {
        return Instancio.of(UtxoId.class)
                .supply(field(UtxoId::getTransactionHashId), TransactionHashTestFactory::getTransactionHash)
                .create();
    }
}
