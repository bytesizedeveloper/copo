package org.acme.blockchain.transaction.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.acme.blockchain.common.model.Address;
import org.acme.blockchain.common.model.Coin;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode
@Builder(toBuilder = true)
public class UtxoModel {

    private UtxoId id;

    private Address recipientAddress;

    private Coin amount;

    private OffsetDateTime createdAt;

    private boolean isSpent;
}
