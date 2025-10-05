package org.acme.wallet.mapper;

import org.acme.test_utility.WalletTestUtility;
import org.acme.wallet.api.contract.WalletResponse;
import org.acme.wallet.model.WalletModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WalletMapperTest {

    @Test
    public void testModelToResponse() {
        // Given
        WalletModel model = WalletTestUtility.wallet;

        WalletResponse expected = WalletTestUtility.response;

        // When
        WalletResponse actual = WalletMapper.INSTANCE.modelToResponse(model);

        // Then
        Assertions.assertEquals(expected, actual);
    }
}
