package org.acme.blockchain.common.service;

import org.acme.blockchain.common.model.CoinModel;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;

@ApplicationScoped
public class RewardService {

    public CoinModel determineRewardAmount() {
        return new CoinModel(BigDecimal.valueOf(1000));
    }
}
