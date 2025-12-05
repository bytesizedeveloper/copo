package org.acme.blockchain.wallet.mapper;

import jooq.tables.records.WalletRecord;
import org.acme.blockchain.common.model.Address;
import org.acme.blockchain.test_common.factory.WalletTestFactory;
import org.acme.blockchain.wallet.api.contract.WalletResponse;
import org.acme.blockchain.wallet.model.WalletModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WalletMapperTest {

    @Test
    void testModelToResponse() {
        // Given
        WalletModel model = WalletTestFactory.getWalletModel();

        WalletResponse expected = WalletResponse.builder()
                .address(model.address().value())
                .publicKeyEncoded(model.publicKeyEncoded())
                .createdAt(model.createdAt())
                .build();

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
        WalletModel model = WalletTestFactory.getWalletModel();

        WalletRecord expected = new WalletRecord(
                null,
                model.address().value(),
                model.publicKeyEncoded(),
                model.createdAt()
        );

        // When
        WalletRecord actual = WalletMapper.INSTANCE.modelToRecord(model);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testRecordToModel() {
        // Given
        WalletRecord record = WalletTestFactory.getWalletRecord();

        WalletModel expected = WalletModel.builder()
                .address(new Address(record.getAddress()))
                .publicKeyEncoded(record.getPublicKeyEncoded())
                .createdAt(record.getCreatedAt())
                .build();

        // When
        WalletModel actual = WalletMapper.INSTANCE.recordToModel(record);

        // Then
        Assertions.assertNull(actual.keyPair());
        Assertions.assertEquals(expected.address(), actual.address());
        Assertions.assertArrayEquals(expected.publicKeyEncoded(), actual.publicKeyEncoded());
        Assertions.assertEquals(expected.createdAt(), actual.createdAt());
    }
}
