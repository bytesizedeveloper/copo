package org.acme.blockchain.transaction.api.contract;

import org.acme.blockchain.transaction.model.UtxoModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.acme.blockchain.test_common.base.ObjectMapperBase;
import org.acme.blockchain.test_common.test_data.TransactionTestData;
import org.acme.blockchain.test_common.test_data.UtxoTestData;
import org.acme.blockchain.test_common.test_data.WalletTestData;
import org.acme.blockchain.transaction.model.enumeration.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TransactionResponseTest extends ObjectMapperBase {

    @Test
    void testJsonMarshalling() throws JsonProcessingException {
        // Given
        TransactionResponse response = TransactionTestData.getResponse();

        String expected = """
               {"hash_id":"%s","sender_address":"%s","recipient_address":"%s","amount":%s,"fee":%s,"type":"%s","inputs":[{"transaction_hash_id":"%s","output_index":"%s","recipient_address":"%s","amount":%s,"created_at":"%s","is_spent":%b},{"transaction_hash_id":"%s","output_index":"%s","recipient_address":"%s","amount":%s,"created_at":"%s","is_spent":%b}],"outputs":[{"transaction_hash_id":"%s","output_index":"%s","recipient_address":"%s","amount":%s,"created_at":"%s","is_spent":%b},{"transaction_hash_id":"%s","output_index":"%s","recipient_address":"%s","amount":%s,"created_at":"%s","is_spent":%b}],"created_at":"%s","signature":"%s"}"""
                .formatted(
                        TransactionTestData.TRANSACTION_HASH_ID,
                        WalletTestData.ADDRESS_ALPHA,
                        WalletTestData.ADDRESS_BETA,
                        DECIMAL_FORMAT.format(BigDecimal.ONE),
                        DECIMAL_FORMAT.format(BigDecimal.ZERO),
                        TransactionType.TRANSFER,
                        UtxoTestData.TRANSACTION_HASH_ALPHA,
                        UtxoModel.OUTPUT_INDEX_RECIPIENT,
                        WalletTestData.ADDRESS_ALPHA,
                        DECIMAL_FORMAT.format(BigDecimal.ONE),
                        UtxoTestData.NOW.format(DATE_TIME_FORMATTER),
                        true,
                        UtxoTestData.TRANSACTION_HASH_BETA,
                        UtxoModel.OUTPUT_INDEX_RECIPIENT,
                        WalletTestData.ADDRESS_ALPHA,
                        DECIMAL_FORMAT.format(BigDecimal.ONE),
                        UtxoTestData.NOW.format(DATE_TIME_FORMATTER),
                        true,
                        TransactionTestData.TRANSACTION_HASH_ID,
                        UtxoModel.OUTPUT_INDEX_RECIPIENT,
                        WalletTestData.ADDRESS_BETA,
                        DECIMAL_FORMAT.format(BigDecimal.ONE),
                        UtxoTestData.NOW.format(DATE_TIME_FORMATTER),
                        false,
                        TransactionTestData.TRANSACTION_HASH_ID,
                        UtxoModel.OUTPUT_INDEX_SENDER,
                        WalletTestData.ADDRESS_ALPHA,
                        DECIMAL_FORMAT.format(BigDecimal.ONE),
                        UtxoTestData.NOW.format(DATE_TIME_FORMATTER),
                        false,
                        TransactionTestData.NOW.format(DATE_TIME_FORMATTER),
                        TransactionTestData.SIGNATURE
                );

        // When
        String actual = objectMapper.writeValueAsString(response);

        // Then
        Assertions.assertEquals(expected, actual);
    }
}
