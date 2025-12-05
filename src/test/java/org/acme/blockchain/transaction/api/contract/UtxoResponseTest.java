package org.acme.blockchain.transaction.api.contract;

import org.acme.blockchain.test_common.base.ObjectMapperBase;
import org.acme.blockchain.test_common.factory.UtxoTestFactory;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class UtxoResponseTest extends ObjectMapperBase {

    @Test
    void testJsonMarshalling() throws Exception {
        // Given
        UtxoResponse response = UtxoTestFactory.getUtxoResponse();

        String expected = """
                {
                    "transaction_hash_id": "%s",
                    "output_index": "%s",
                    "recipient_address": "%s",
                    "amount": %s,
                    "created_at": "%s",
                    "is_spent": %b
                }"""
                .formatted(
                        response.transactionHashId(),
                        response.outputIndex(),
                        response.recipientAddress(),
                        response.amount(),
                        response.createdAt(),
                        response.isSpent()
                );

        // When
        String actual = objectMapper.writeValueAsString(response);

        // Then
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT_ORDER);
    }
}
