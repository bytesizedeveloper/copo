package org.acme.blockchain.wallet.mapper;

import jooq.tables.records.WalletRecord;
import org.acme.blockchain.common.mapper.AddressMapper;
import org.acme.blockchain.wallet.api.contract.WalletResponse;
import org.acme.blockchain.wallet.model.WalletModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = AddressMapper.class)
public interface WalletMapper {

    WalletMapper INSTANCE = Mappers.getMapper(WalletMapper.class);

    WalletResponse modelToResponse(WalletModel wallet);

    @Mapping(target = "id", ignore = true)
    WalletRecord modelToRecord(WalletModel wallet);

    @Mapping(target = "keyPair", ignore = true)
    WalletModel recordToModel(WalletRecord record);
}
