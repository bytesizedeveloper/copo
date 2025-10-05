package org.acme.wallet.api.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.acme.test_utility.WalletTestUtility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;

public class WalletResponseTest {

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Test
        public void testJsonMarshalling() throws Exception {
            // Given
            String address = WalletTestUtility.address;

            byte[] publicKeyEncoded = WalletTestUtility.wallet.getPublicKeyEncoded();
            String base64 = Base64.getEncoder().encodeToString(publicKeyEncoded);

            String expected = """
                    {"address":"%s","public_key":"%s"}""".formatted(address, base64);

            WalletResponse response = WalletTestUtility.response;

            // When
            String actual = objectMapper.writeValueAsString(response);

            // Then
            Assertions.assertEquals(expected, actual);
        }
}
