package org.acme.blockchain.transaction.mapper;

import org.acme.blockchain.transaction.model.TransactionSignature;
import org.mapstruct.Mapper;

@Mapper
public interface TransactionSignatureMapper {

    default TransactionSignature map(String signature) {
        return new TransactionSignature(signature);
    }

    default String map(TransactionSignature signature) {
        return signature.value();
    }
}
