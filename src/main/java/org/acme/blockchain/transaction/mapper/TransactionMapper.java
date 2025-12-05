package org.acme.blockchain.transaction.mapper;

import jooq.tables.records.TransactionRecord;
import org.acme.blockchain.common.mapper.AddressMapper;
import org.acme.blockchain.common.mapper.CoinMapper;
import org.acme.blockchain.transaction.api.contract.TransactionResponse;
import org.acme.blockchain.transaction.api.contract.TransferRequest;
import org.acme.blockchain.transaction.model.RewardModel;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransferModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        TransactionHashMapper.class,
        AddressMapper.class,
        CoinMapper.class,
        UtxoMapper.class,
        TransactionSignatureMapper.class,
        ByteMapper.class
})
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(target = "hashId", ignore = true)
    @Mapping(target = "senderPublicKeyEncoded", ignore = true)
    @Mapping(target = "fee", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "inputs", ignore = true)
    @Mapping(target = "outputs", ignore = true)
    @Mapping(target = "inputIds", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "signature", ignore = true)
    @Mapping(target = "status", ignore = true)
    TransferModel requestToModel(TransferRequest transaction);

    TransactionResponse modelToResponse(TransferModel transfer);

    TransactionResponse modelToResponse(RewardModel reward);

    @Mapping(target = "id", ignore = true)
    TransactionRecord modelToRecord(TransactionModel transaction);

    @Mapping(target = "inputs", ignore = true)
    @Mapping(target = "outputs", ignore = true)
    @Mapping(target = "type", constant = "TRANSFER")
    @Mapping(target = "status", constant = "MINED")
    TransferModel recordToTransferModel(TransactionRecord transaction);

    @Mapping(target = "inputs", ignore = true)
    @Mapping(target = "outputs", ignore = true)
    @Mapping(target = "type", constant = "REWARD")
    @Mapping(target = "status", constant = "MINED")
    RewardModel recordToRewardModel(TransactionRecord transaction);

    default TransactionResponse modelToResponse(TransactionModel transaction) {
        if (transaction instanceof TransferModel) {
            return modelToResponse((TransferModel) transaction);
        } else if (transaction instanceof RewardModel) {
            return modelToResponse((RewardModel) transaction);
        } else {
            throw new IllegalStateException();
        }
    }


}
