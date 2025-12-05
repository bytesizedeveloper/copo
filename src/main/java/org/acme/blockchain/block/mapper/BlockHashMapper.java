package org.acme.blockchain.block.mapper;

import org.acme.blockchain.block.model.BlockHash;
import org.mapstruct.Mapper;

@Mapper
public interface BlockHashMapper {

    default BlockHash map(String hash) {
        return new BlockHash(hash);
    }

    default String map(BlockHash hash) {
        return hash.value();
    }
}
