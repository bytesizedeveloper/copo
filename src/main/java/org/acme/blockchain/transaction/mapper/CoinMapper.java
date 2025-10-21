package org.acme.blockchain.transaction.mapper;

import org.acme.blockchain.transaction.model.CoinModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper
public interface CoinMapper {

    CoinMapper INSTANCE = Mappers.getMapper(CoinMapper.class);

    CoinModel bigDecimalToCoin(BigDecimal value);

    default BigDecimal coinToBigDecimal(CoinModel coin) {
        if (coin == null) {
            return null;
        }
        return coin.value();
    }
}
