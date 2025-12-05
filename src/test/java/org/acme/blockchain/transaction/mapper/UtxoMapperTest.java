package org.acme.blockchain.transaction.mapper;

import jooq.tables.records.UtxoRecord;
import org.acme.blockchain.common.model.Address;
import org.acme.blockchain.common.model.Coin;
import org.acme.blockchain.test_common.factory.UtxoTestFactory;
import org.acme.blockchain.transaction.api.contract.UtxoResponse;
import org.acme.blockchain.transaction.model.TransactionHash;
import org.acme.blockchain.transaction.model.UtxoId;
import org.acme.blockchain.transaction.model.UtxoModel;
import org.acme.blockchain.transaction.model.enumeration.OutputIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtxoMapperTest {

    @Test
    void testModelToResponse() {
        // Given
        UtxoModel utxo = UtxoTestFactory.getUtxoModel();

        UtxoResponse expected = UtxoResponse.builder()
                .transactionHashId(utxo.getId().getTransactionHashId().value())
                .outputIndex(utxo.getId().getOutputIndex().getIndex())
                .recipientAddress(utxo.getRecipientAddress().value())
                .amount(utxo.getAmount().value())
                .createdAt(utxo.getCreatedAt())
                .isSpent(utxo.isSpent())
                .build();

        // When
        UtxoResponse actual = UtxoMapper.INSTANCE.modelToResponse(utxo);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testModelToRecord() {
        // Given
        UtxoModel utxo = UtxoTestFactory.getUtxoModel();

        UtxoRecord expected = new UtxoRecord(
                null,
                utxo.getId().getTransactionHashId().value(),
                utxo.getId().getOutputIndex().getIndex(),
                utxo.getRecipientAddress().value(),
                utxo.getAmount().value(),
                utxo.getCreatedAt(),
                utxo.isSpent()
        );

        // When
        UtxoRecord actual = UtxoMapper.INSTANCE.modelToRecord(utxo);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testRecordToModel() {
        // Given
        UtxoRecord record = UtxoTestFactory.getUtxoRecord();

        UtxoId id = new UtxoId(new TransactionHash(record.getTransactionHashId()), OutputIndex.fromIndex(record.getOutputIndex()));

        UtxoModel expected = UtxoModel.builder()
                .id(id)
                .recipientAddress(new Address(record.getRecipientAddress()))
                .amount(new Coin(record.getAmount()))
                .createdAt(record.getCreatedAt())
                .isSpent(record.getIsSpent())
                .build();

        // When
        UtxoModel actual = UtxoMapper.INSTANCE.recordToModel(record);

        // Then
        Assertions.assertEquals(expected, actual);
    }
}
