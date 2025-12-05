package org.acme.blockchain.transaction.mapper;

import org.acme.blockchain.transaction.model.TransactionHash;
import org.mapstruct.Mapper;

@Mapper
public interface TransactionHashMapper {

    default TransactionHash map(String hash) {
        return new TransactionHash(hash);
    }

    default String map(TransactionHash hash) {
        return hash.value();
    }
}
