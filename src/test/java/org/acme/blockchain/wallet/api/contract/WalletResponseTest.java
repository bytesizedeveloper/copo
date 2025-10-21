package org.acme.blockchain.wallet.api.contract;

import org.acme.blockchain.test_common.base.ObjectMapperBase;
import org.acme.blockchain.test_common.test_data.WalletTestData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;

public class WalletResponseTest extends ObjectMapperBase {

        @Test
        void testJsonMarshalling() throws Exception {
            // Given
            WalletResponse response = WalletTestData.getResponse();

            String expected = """
                    {"address":"%s","public_key":"%s","created_at":"%s"}"""
                    .formatted(
                            WalletTestData.ADDRESS_ALPHA,
                            Base64.getEncoder().encodeToString(WalletTestData.getWallet().getPublicKeyEncoded()),
                            WalletTestData.NOW.format(DATE_TIME_FORMATTER));

            // When
            String actual = objectMapper.writeValueAsString(response);

            // Then
            Assertions.assertEquals(expected, actual);
        }
}
