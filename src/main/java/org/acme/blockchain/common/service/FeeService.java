package org.acme.blockchain.common.service;

import org.acme.blockchain.common.model.CoinModel;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;

@ApplicationScoped
public class FeeService {

    public CoinModel calculateFee() {
        return new CoinModel(BigDecimal.valueOf(0.00000001));
    }
}
