package org.acme.blockchain.test_common.test_data;

import jooq.tables.records.WalletRecord;
import org.acme.blockchain.common.model.AddressModel;
import org.acme.blockchain.wallet.utility.KeyPairUtility;
import org.acme.blockchain.common.utility.TimestampUtility;
import org.acme.blockchain.wallet.api.contract.WalletResponse;
import org.acme.blockchain.wallet.model.WalletModel;

import java.security.KeyPair;
import java.time.OffsetDateTime;

public final class WalletTestData {

    public static final AddressModel ADDRESS_ALPHA =
            new AddressModel("COPO_abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789");

    public static final AddressModel ADDRESS_BETA =
            new AddressModel("COPO_0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef");

    public static final KeyPair KEYPAIR_ALPHA = KeyPairUtility.generateKeyPair();

    public static final KeyPair KEYPAIR_BETA = KeyPairUtility.generateKeyPair();

    public static final OffsetDateTime NOW = TimestampUtility.getOffsetDateTimeNow();

    public static WalletModel getWalletAlpha() {
        return WALLET_ALPHA.toBuilder().build();
    }

    public static WalletModel getWalletBeta() {
        return WALLET_BETA.toBuilder().build();
    }

    public static WalletResponse getResponseAlpha() {
        return RESPONSE_ALPHA.toBuilder().build();
    }

    public static WalletRecord getRecordPreInsert() {
        return RECORD_PRE_INSERT;
    }

    public static WalletRecord getRecordPostInsert() {
        return RECORD_POST_INSERT;
    }

    private static final WalletModel WALLET_ALPHA = WalletModel.builder()
            .id(1)
            .keyPair(KEYPAIR_ALPHA)
            .address(ADDRESS_ALPHA)
            .publicKeyEncoded(KEYPAIR_ALPHA.getPublic().getEncoded())
            .createdAt(NOW)
            .build();

    private static final WalletModel WALLET_BETA = WalletModel.builder()
            .id(2)
            .keyPair(KEYPAIR_BETA)
            .address(ADDRESS_BETA)
            .publicKeyEncoded(KEYPAIR_BETA.getPublic().getEncoded())
            .createdAt(NOW)
            .build();

    private static final WalletResponse RESPONSE_ALPHA = WalletResponse.builder()
            .address(ADDRESS_ALPHA.value())
            .publicKeyEncoded(KEYPAIR_ALPHA.getPublic().getEncoded())
            .createdAt(NOW)
            .build();

    private static final WalletRecord RECORD_PRE_INSERT = new WalletRecord(
            null,
            ADDRESS_ALPHA.value(),
            KEYPAIR_ALPHA.getPublic().getEncoded(),
            NOW
    );

    private static final WalletRecord RECORD_POST_INSERT = new WalletRecord(
            1L,
            ADDRESS_ALPHA.value(),
            KEYPAIR_ALPHA.getPublic().getEncoded(),
            NOW
    );
}
