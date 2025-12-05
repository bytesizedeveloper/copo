package org.acme.blockchain.wallet.model;

import lombok.Builder;
import org.acme.blockchain.common.model.Address;

import java.security.KeyPair;
import java.time.OffsetDateTime;

@Builder(toBuilder = true)
public record WalletModel(

        KeyPair keyPair,

        Address address,

        byte[] publicKeyEncoded,

        OffsetDateTime createdAt
) {}
