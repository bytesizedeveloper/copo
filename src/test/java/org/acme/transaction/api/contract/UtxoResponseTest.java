package org.acme.transaction.api.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.acme.test_common.base.ObjectMapperBase;
import org.acme.test_common.test_data.UtxoTestData;
import org.acme.test_common.test_data.WalletTestData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class UtxoResponseTest extends ObjectMapperBase {

    @Test
    public void testJsonMarshalling() throws JsonProcessingException {
        // Given
        UtxoResponse response = UtxoTestData.getInputResponseAlpha();

        String expected = """
               {"transaction_hash_id":"%s","output_index":"%s","recipient_address":"%s","amount":%s,"created_at":"%s","is_spent":%b}"""
                .formatted(
                        UtxoTestData.TRANSACTION_HASH_ALPHA,
                        UtxoTestData.OUTPUT_INDEX_RECIPIENT,
                        WalletTestData.ADDRESS_ALPHA,
                        DECIMAL_FORMAT.format(BigDecimal.ONE),
                        UtxoTestData.NOW.format(DATE_TIME_FORMATTER),
                        true
                );

        // When
        String actual = objectMapper.writeValueAsString(response);

        // Then
        Assertions.assertEquals(expected, actual);
    }
}
