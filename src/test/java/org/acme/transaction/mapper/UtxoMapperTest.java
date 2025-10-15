package org.acme.transaction.mapper;

import jooq.tables.records.UtxoRecord;
import org.acme.test_common.test_data.UtxoTestData;
import org.acme.transaction.api.contract.UtxoResponse;
import org.acme.transaction.model.UtxoModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtxoMapperTest {

    @Test
    public void testModelToResponse() {
        // Given
        UtxoModel utxo = UtxoTestData.getInputUtxoAlpha();

        UtxoResponse expected = UtxoTestData.getInputResponseAlpha();

        // When
        UtxoResponse actual = UtxoMapper.INSTANCE.modelToResponse(utxo);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testModelToRecord() {
        // Given
        UtxoModel utxo = UtxoTestData.getInputUtxoAlpha();

        UtxoRecord expected = UtxoTestData.getInputRecordAlphaPreInsert();

        // When
        UtxoRecord actual = UtxoMapper.INSTANCE.modelToRecord(utxo);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testRecordToModel() {
        // Given
        UtxoRecord record = UtxoTestData.getInputRecordAlphaPostInsert();

        UtxoModel expected = UtxoTestData.getInputUtxoAlpha();

        // When
        UtxoModel actual = UtxoMapper.INSTANCE.recordToModel(record);

        // Then
        Assertions.assertEquals(expected, actual);
    }
}
