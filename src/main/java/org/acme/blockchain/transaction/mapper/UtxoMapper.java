package org.acme.blockchain.transaction.mapper;

import jooq.tables.records.UtxoRecord;
import org.acme.blockchain.common.mapper.AddressMapper;
import org.acme.blockchain.common.mapper.CoinMapper;
import org.acme.blockchain.transaction.api.contract.UtxoResponse;
import org.acme.blockchain.transaction.model.TransactionHash;
import org.acme.blockchain.transaction.model.UtxoId;
import org.acme.blockchain.transaction.model.UtxoModel;
import org.acme.blockchain.transaction.model.enumeration.OutputIndex;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        TransactionHashMapper.class,
        AddressMapper.class,
        CoinMapper.class,
        OutputIndexMapper.class
})
public interface UtxoMapper {

    UtxoMapper INSTANCE = Mappers.getMapper(UtxoMapper.class);

    @Mapping(source = "id.transactionHashId", target = "transactionHashId")
    @Mapping(source = "id.outputIndex", target = "outputIndex")
    @Mapping(source = "spent", target = "isSpent")
    UtxoResponse modelToResponse(UtxoModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "id.transactionHashId", target = "transactionHashId")
    @Mapping(source = "id.outputIndex", target = "outputIndex")
    @Mapping(source = "spent", target = "isSpent")
    UtxoRecord modelToRecord(UtxoModel model);

    @Mapping(source = ".", target = "id", qualifiedByName = "mapUtxoId")
    UtxoModel recordToModel(UtxoRecord record);

    @Named("mapUtxoId")
    default UtxoId mapUtxoId(UtxoRecord record) {
        TransactionHash transactionHash = new TransactionHash(record.getTransactionHashId());
        OutputIndex outputIndex = OutputIndex.fromIndex(record.getOutputIndex());

        return new UtxoId(transactionHash, outputIndex);
    }
}
