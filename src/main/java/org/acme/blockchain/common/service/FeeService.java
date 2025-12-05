package org.acme.blockchain.common.service;

import org.acme.blockchain.common.model.Coin;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;

@ApplicationScoped
public class FeeService {

    public Coin calculateFee() {
        return new Coin(BigDecimal.valueOf(0.12345678));
    }
}
