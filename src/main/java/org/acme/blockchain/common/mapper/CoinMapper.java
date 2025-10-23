package org.acme.blockchain.common.mapper;

import org.acme.blockchain.common.model.CoinModel;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper
public interface CoinMapper {

    default CoinModel map(BigDecimal coin) {
        return new CoinModel(coin);
    }

    default BigDecimal map(CoinModel coin) {
        return coin.value();
    }
}
