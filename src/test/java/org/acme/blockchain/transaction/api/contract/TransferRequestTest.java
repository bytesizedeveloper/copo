package org.acme.blockchain.transaction.api.contract;

import org.acme.blockchain.test_common.base.ObjectMapperBase;
import org.acme.blockchain.test_common.factory.TransactionTestFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransferRequestTest extends ObjectMapperBase {

    @Test
    void testJsonUnmarshalling() throws Exception {
        // Given
        TransferRequest expected = TransactionTestFactory.getTransferRequest();

        String json = """
                {
                    "sender_address": "%s",
                    "recipient_address": "%s",
                    "amount": %s
                }"""
                .formatted(
                        expected.senderAddress(),
                        expected.recipientAddress(),
                        expected.amount()
                );

        // When
        TransferRequest actual = objectMapper.readValue(json, TransferRequest.class);

        // Then
        Assertions.assertEquals(expected, actual);
    }
}
