package org.acme.blockchain.test_common.factory;

import org.acme.blockchain.transaction.model.TransactionHash;
import org.instancio.Instancio;

import static org.instancio.Select.all;
import static org.instancio.Select.field;

public final class TransactionHashTestFactory {

    public static TransactionHash getTransactionHash() {
        return Instancio.of(TransactionHash.class)
                .supply(field(TransactionHash::value), TransactionHashTestFactory::getTransactionHashString)
                .create();
    }

    public static String getTransactionHashString() {
        return Instancio.of(String.class)
                .generate(all(String.class),
                        gen -> gen.string().hex().lowerCase().length(64))
                .create();
    }
}
