package org.acme.blockchain.test_common.factory;

import jooq.tables.records.WalletRecord;
import org.acme.blockchain.wallet.api.contract.WalletResponse;
import org.acme.blockchain.wallet.model.WalletModel;
import org.acme.blockchain.wallet.utility.KeyPairUtility;
import org.instancio.Instancio;

import static org.instancio.Select.field;

public final class WalletTestFactory {

    public static WalletModel getWalletModel() {
        return Instancio.of(WalletModel.class)
                .supply(field(WalletModel::keyPair), KeyPairUtility::generateKeyPair)
                .supply(field(WalletModel::address), () -> AddressTestFactory.getAddress())
                .create();
    }

    public static WalletResponse getWalletResponse() {
        return Instancio.of(WalletResponse.class)
                .supply(field(WalletResponse::address), AddressTestFactory::getAddressString)
                .create();
    }

    public static WalletRecord getWalletRecord() {
        return new WalletRecord(
                Instancio.of(Long.class).create(),
                AddressTestFactory.getAddressString(),
                Instancio.of(byte[].class).create(),
                TimestampTestFactory.generateTimestamp()
        );
    }
}
