package org.acme.blockchain.test_common.factory;

import org.acme.blockchain.transaction.model.TransactionSignature;
import org.instancio.Instancio;

import static org.instancio.Select.all;
import static org.instancio.Select.field;

public final class TransactionSignatureTestFactory {

    public static TransactionSignature getTransactionSignature() {
        return Instancio.of(TransactionSignature.class)
                .supply(field(TransactionSignature::value), TransactionSignatureTestFactory::getTransactionSignatureString)
                .create();
    }

    public static String getTransactionSignatureString() {
        return Instancio.of(String.class)
                .generate(all(String.class),
                        gen -> gen.string().hex().lowerCase().length(9254))
                .create();
    }

    public static byte[] getTransactionSignatureBytes() {
        return getTransactionSignature().toBytes();
    }
}
