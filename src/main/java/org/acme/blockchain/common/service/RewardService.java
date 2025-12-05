package org.acme.blockchain.common.service;

import org.acme.blockchain.common.model.Coin;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;

@ApplicationScoped
public class RewardService {

    public Coin determineRewardAmount() {
        return new Coin(BigDecimal.valueOf(1000));
    }
}
