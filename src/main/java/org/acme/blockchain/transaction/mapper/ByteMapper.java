package org.acme.blockchain.transaction.mapper;

import org.mapstruct.Mapper;

import java.util.Base64;

@Mapper
public interface ByteMapper {

    default String map(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
