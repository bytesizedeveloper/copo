package org.acme.test_utility;

import org.acme.common.utility.KeyPairUtility;
import org.acme.wallet.api.contract.WalletResponse;
import org.acme.wallet.mapper.WalletMapper;
import org.acme.wallet.model.WalletModel;

import java.security.KeyPair;

public final class WalletTestUtility {

    public static final String address = "COPO_abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789";

    public static final WalletModel wallet = new WalletModel(address, generateKeyPair());

    public static final WalletResponse response = modelToResponse(wallet);

    public static KeyPair generateKeyPair() {
        return KeyPairUtility.generateKeyPair();
    }

    public static WalletResponse modelToResponse(WalletModel model) {
        return WalletMapper.INSTANCE.modelToResponse(model);
    }
}
