package org.acme.blockchain.transaction.mapper;

import jooq.tables.records.TransactionRecord;
import org.acme.blockchain.test_common.test_data.TransactionTestData;
import org.acme.blockchain.transaction.api.contract.TransactionRequest;
import org.acme.blockchain.transaction.api.contract.TransactionResponse;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransactionMapperTest {

    @Test
    void testRequestToModel() {
        // Given
        TransactionRequest request = TransactionTestData.getRequest();

        TransactionModel expected = TransactionTestData.getTransactionPreInitialise();

        // When
        TransactionModel actual = TransactionMapper.INSTANCE.requestToModel(request);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testModelToResponse() {
        // Given
        TransactionModel model = TransactionTestData.getTransactionPostInitialise();

        TransactionResponse expected = TransactionTestData.getResponse();

        // When
        TransactionResponse actual = TransactionMapper.INSTANCE.modelToResponse(model);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testModelToRecord() {
        // Given
        TransactionModel model = TransactionTestData.getTransactionPostInitialise();

        TransactionRecord expected = TransactionTestData.getRecordPreInsert();

        // When
        TransactionRecord actual = TransactionMapper.INSTANCE.modelToRecord(model);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testRecordToModel() {
        // Given
        TransactionRecord record = TransactionTestData.getRecordPostInsert();

        // Inputs and outputs are not mapped
        TransactionModel expected = TransactionTestData.getTransactionPostInitialise().toBuilder()
                .inputs(null)
                .outputs(null)
                .build();

        // When
        TransactionModel actual = TransactionMapper.INSTANCE.recordToModel(record);

        // Then
        Assertions.assertEquals(expected, actual);
    }
}
