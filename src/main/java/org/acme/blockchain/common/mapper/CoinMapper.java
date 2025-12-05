package org.acme.blockchain.common.mapper;

import org.acme.blockchain.common.model.Coin;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper
public interface CoinMapper {

    default Coin map(BigDecimal coin) {
        return new Coin(coin);
    }

    default BigDecimal map(Coin coin) {
        return coin.value();
    }
}
