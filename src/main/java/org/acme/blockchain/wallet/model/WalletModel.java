package org.acme.blockchain.wallet.model;

import lombok.Builder;
import org.acme.blockchain.common.model.AddressModel;

import java.security.KeyPair;
import java.time.OffsetDateTime;

@Builder(toBuilder = true)
public record WalletModel(

        long id,

        KeyPair keyPair,

        AddressModel address,

        byte[] publicKeyEncoded,

        OffsetDateTime createdAt
) {}
