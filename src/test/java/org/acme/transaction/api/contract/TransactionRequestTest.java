package org.acme.transaction.api.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.acme.test_common.base.ObjectMapperBase;
import org.acme.test_common.test_data.TransactionTestData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransactionRequestTest extends ObjectMapperBase {

    @Test
    public void testJsonUnmarshalling() throws JsonProcessingException {
        // Given
        TransactionRequest expected = TransactionTestData.getRequest();

        String json = """
                {
                    "sender_address": "%s",
                    "recipient_address": "%s",
                    "amount" :%f
                }"""
                .formatted(expected.getSenderAddress(), expected.getRecipientAddress(), expected.getAmount());

        // When
        TransactionRequest actual = objectMapper.readValue(json, TransactionRequest.class);

        // Then
        Assertions.assertEquals(expected, actual);
    }
}
