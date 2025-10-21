package org.acme.blockchain.wallet.mapper;

import org.acme.blockchain.test_common.test_data.WalletTestData;
import org.acme.blockchain.wallet.api.contract.WalletResponse;
import org.acme.blockchain.wallet.model.WalletModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WalletMapperTest {

    @Test
    void testModelToResponse() {
        // Given
        WalletModel model = WalletTestData.getWallet();

        WalletResponse expected = WalletTestData.getResponse();

        // When
        WalletResponse actual = WalletMapper.INSTANCE.modelToResponse(model);

        // Then
        Assertions.assertEquals(expected, actual);
    }
}
