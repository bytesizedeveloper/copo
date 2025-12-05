package org.acme.blockchain.block.mapper;

import org.acme.blockchain.block.api.contract.BlockResponse;
import org.acme.blockchain.block.model.BlockModel;
import org.acme.blockchain.common.mapper.CoinMapper;
import org.acme.blockchain.transaction.mapper.TransactionMapper;
import jooq.tables.records.BlockRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        BlockHashMapper.class,
        CoinMapper.class,
        TransactionMapper.class
})
public interface BlockMapper {

    BlockMapper INSTANCE = Mappers.getMapper(BlockMapper.class);

    BlockResponse modelToResponse(BlockModel blockModel);

    @Mapping(target = "id", ignore = true)
    BlockRecord modelToRecord(BlockModel blockModel);

    @Mapping(target = "transactions", ignore = true)
    BlockModel recordToModel(BlockRecord blockRecord);
}
