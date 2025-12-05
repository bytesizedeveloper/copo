package org.acme.blockchain.test_common.factory;

import org.acme.blockchain.common.model.Coin;
import org.instancio.Instancio;

import java.math.BigDecimal;

import static org.instancio.Select.all;
import static org.instancio.Select.field;

public final class CoinTestFactory {

    public static Coin getCoin() {
        return Instancio.of(Coin.class)
                .supply(field(Coin::value), CoinTestFactory::getCoinBigDecimal)
                .create();
    }

    public static BigDecimal getCoinBigDecimal() {
        return Instancio.of(BigDecimal.class)
                .generate(all(BigDecimal.class),
                        gen -> gen.math().bigDecimal().min(Coin.MINIMUM).max(Coin.MAXIMUM).scale(Coin.SCALE))
                .create();
    }
}
