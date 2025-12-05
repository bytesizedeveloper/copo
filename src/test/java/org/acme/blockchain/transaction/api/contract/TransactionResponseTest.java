package org.acme.blockchain.transaction.api.contract;

import org.acme.blockchain.test_common.base.ObjectMapperBase;
import org.acme.blockchain.test_common.factory.TransactionTestFactory;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class TransactionResponseTest extends ObjectMapperBase {

    @Test
    void testJsonMarshalling() throws Exception {
        // Given
        TransactionResponse response = TransactionTestFactory.getTransferResponse();

        String expected = """
               {
                    "hash_id": "%s",
                    "sender_address": "%s",
                    "recipient_address": "%s",
                    "sender_public_key": "%s",
                    "amount": %s,
                    "fee": %s,
                    "type": "%s",
                    "inputs": [
                        {
                            "transaction_hash_id": "%s",
                            "output_index": "%s",
                            "recipient_address": "%s",
                            "amount": %s,
                            "created_at": "%s",
                            "is_spent": %b
                        },
                        {
                            "transaction_hash_id": "%s",
                            "output_index": "%s",
                            "recipient_address": "%s",
                            "amount": %s,
                            "created_at": "%s",
                            "is_spent": %b
                        }
                    ],
                    "outputs": [
                        {
                            "transaction_hash_id": "%s",
                            "output_index": "%s",
                            "recipient_address": "%s",
                            "amount": %s,
                            "created_at": "%s",
                            "is_spent": %b
                        },
                        {
                            "transaction_hash_id": "%s",
                            "output_index": "%s",
                            "recipient_address": "%s",
                            "amount": %s,
                            "created_at": "%s",
                            "is_spent": %b
                        }
                    ],
                    "created_at": "%s",
                    "signature": "%s",
                    "status": "%s"
                }"""
                .formatted(
                        response.hashId(),
                        response.senderAddress(),
                        response.recipientAddress(),
                        response.senderPublicKeyEncoded(),
                        response.amount(),
                        response.fee(),
                        response.type(),
                        response.inputs().get(0).transactionHashId(),
                        response.inputs().get(0).outputIndex(),
                        response.inputs().get(0).recipientAddress(),
                        response.inputs().get(0).amount(),
                        response.inputs().get(0).createdAt(),
                        response.inputs().get(0).isSpent(),
                        response.inputs().get(1).transactionHashId(),
                        response.inputs().get(1).outputIndex(),
                        response.inputs().get(1).recipientAddress(),
                        response.inputs().get(1).amount(),
                        response.inputs().get(1).createdAt(),
                        response.inputs().get(1).isSpent(),
                        response.outputs().get(0).transactionHashId(),
                        response.outputs().get(0).outputIndex(),
                        response.outputs().get(0).recipientAddress(),
                        response.outputs().get(0).amount(),
                        response.outputs().get(0).createdAt(),
                        response.outputs().get(0).isSpent(),
                        response.outputs().get(1).transactionHashId(),
                        response.outputs().get(1).outputIndex(),
                        response.outputs().get(1).recipientAddress(),
                        response.outputs().get(1).amount(),
                        response.outputs().get(1).createdAt(),
                        response.outputs().get(1).isSpent(),
                        response.createdAt(),
                        response.signature(),
                        response.status()
                );

        // When
        String actual = objectMapper.writeValueAsString(response);

        // Then
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT_ORDER);
    }
}
