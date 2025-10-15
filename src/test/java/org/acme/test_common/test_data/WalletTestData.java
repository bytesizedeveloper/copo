package org.acme.test_common.test_data;

import org.acme.common.utility.KeyPairUtility;
import org.acme.common.utility.TimestampUtility;
import org.acme.wallet.api.contract.WalletResponse;
import org.acme.wallet.model.WalletModel;

import java.security.KeyPair;
import java.time.OffsetDateTime;

public final class WalletTestData {

    public static final String ADDRESS_ALPHA = "COPO_abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789";

    public static final String ADDRESS_BETA = "COPO_0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

    public static final KeyPair KEYPAIR_ALPHA = KeyPairUtility.generateKeyPair();

    public static final OffsetDateTime NOW = TimestampUtility.getOffsetDateTimeNow();

    public static WalletModel getWallet() {
        return WALLET.toBuilder().build();
    }

    public static WalletResponse getResponse() {
        return RESPONSE.toBuilder().build();
    }

    private static final WalletModel WALLET = WalletModel.builder()
            .id(1)
            .address(ADDRESS_ALPHA)
            .keyPair(KEYPAIR_ALPHA)
            .createdAt(NOW)
            .build();

    private static final WalletResponse RESPONSE = WalletResponse.builder()
            .address(ADDRESS_ALPHA)
            .publicKeyEncoded(KEYPAIR_ALPHA.getPublic().getEncoded())
            .createdAt(NOW)
            .build();
}
