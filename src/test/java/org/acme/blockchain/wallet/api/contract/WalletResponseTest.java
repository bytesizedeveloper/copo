package org.acme.blockchain.wallet.api.contract;

import org.acme.blockchain.test_common.base.ObjectMapperBase;
import org.acme.blockchain.test_common.factory.WalletTestFactory;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.Base64;

public class WalletResponseTest extends ObjectMapperBase {

        @Test
        void testJsonMarshalling() throws Exception {
            // Given
            WalletResponse response = WalletTestFactory.getWalletResponse();

            String expected = """
                    {
                        "address": "%s",
                        "public_key": "%s",
                        "created_at": "%s"
                    }"""
                    .formatted(
                            response.address(),
                            Base64.getEncoder().encodeToString(response.publicKeyEncoded()),
                            response.createdAt());

            // When
            String actual = objectMapper.writeValueAsString(response);

            // Then
            JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT_ORDER);
        }
}
