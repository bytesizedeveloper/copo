package org.acme.blockchain.wallet.mapper;

import jooq.tables.records.WalletRecord;
import org.acme.blockchain.test_common.test_data.WalletTestData;
import org.acme.blockchain.wallet.api.contract.WalletResponse;
import org.acme.blockchain.wallet.model.WalletModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WalletMapperTest {

    @Test
    void testModelToResponse() {
        // Given
        WalletModel model = WalletTestData.getWalletAlpha();

        WalletResponse expected = WalletTestData.getResponseAlpha();

        // When
        WalletResponse actual = WalletMapper.INSTANCE.modelToResponse(model);

        // Then
        Assertions.assertEquals(expected.address(), actual.address());
        Assertions.assertArrayEquals(expected.publicKeyEncoded(), actual.publicKeyEncoded());
        Assertions.assertEquals(expected.createdAt(), actual.createdAt());
    }

    @Test
    void testModelToRecord() {
        // Given
        WalletModel model = WalletTestData.getWalletAlpha();

        WalletRecord expected = WalletTestData.getRecordPreInsert();

        // When
        WalletRecord actual = WalletMapper.INSTANCE.modelToRecord(model);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testRecordToModel() {
        // Given
        WalletRecord record = WalletTestData.getRecordPostInsert();

        WalletModel expected = WalletTestData.getWalletAlpha();

        // When
        WalletModel actual = WalletMapper.INSTANCE.recordToModel(record);

        // Then
        Assertions.assertNull(actual.keyPair());
        Assertions.assertEquals(expected.address(), actual.address());
        Assertions.assertArrayEquals(expected.publicKeyEncoded(), actual.publicKeyEncoded());
        Assertions.assertEquals(expected.createdAt(), actual.createdAt());
    }
}
