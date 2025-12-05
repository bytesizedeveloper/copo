package org.acme.blockchain.transaction.mapper;

import jooq.tables.records.TransactionRecord;
import org.acme.blockchain.common.model.Address;
import org.acme.blockchain.common.model.Coin;
import org.acme.blockchain.test_common.factory.TransactionTestFactory;
import org.acme.blockchain.transaction.api.contract.TransactionResponse;
import org.acme.blockchain.transaction.api.contract.TransferRequest;
import org.acme.blockchain.transaction.model.RewardModel;
import org.acme.blockchain.transaction.model.TransactionHash;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransactionSignature;
import org.acme.blockchain.transaction.model.TransferModel;
import org.acme.blockchain.transaction.model.enumeration.TransactionStatus;
import org.acme.blockchain.transaction.model.enumeration.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.List;

public class TransactionMapperTest {

    @Test
    void testTransferRequestToModel() {
        // Given
        TransferRequest request = TransactionTestFactory.getTransferRequest();

        TransferModel expected = TransferModel.builder()
                .senderAddress(new Address(request.senderAddress()))
                .recipientAddress(new Address(request.recipientAddress()))
                .amount(new Coin(request.amount()))
                .build();

        // When
        TransferModel actual = TransactionMapper.INSTANCE.requestToModel(request);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testTransferModelToResponse() {
        // Given
        TransferModel model = TransactionTestFactory.getTransferModel();

        TransactionResponse expected = TransactionResponse.builder()
                .hashId(model.getHashId().value())
                .senderAddress(model.getSenderAddress().value())
                .recipientAddress(model.getRecipientAddress().value())
                .senderPublicKeyEncoded(Base64.getEncoder().encodeToString(model.getSenderPublicKeyEncoded()))
                .amount(model.getAmount().value())
                .fee(model.getFee().value())
                .type(model.getType())
                .inputs(model.getInputs().stream().map(UtxoMapper.INSTANCE::modelToResponse).toList())
                .outputs(model.getOutputs().stream().map(UtxoMapper.INSTANCE::modelToResponse).toList())
                .createdAt(model.getCreatedAt())
                .signature(model.getSignature().value())
                .status(model.getStatus())
                .build();

        // When
        TransactionResponse actual = TransactionMapper.INSTANCE.modelToResponse(model);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testRewardModelToResponse() {
        // Given
        RewardModel model = TransactionTestFactory.getRewardModel();

        TransactionResponse expected = TransactionResponse.builder()
                .hashId(model.getHashId().value())
                .senderAddress(model.getSenderAddress().value())
                .recipientAddress(model.getRecipientAddress().value())
                .senderPublicKeyEncoded(Base64.getEncoder().encodeToString(model.getSenderPublicKeyEncoded()))
                .amount(model.getAmount().value())
                .fee(model.getFee().value())
                .type(model.getType())
                .inputs(List.of())
                .outputs(model.getOutputs().stream().map(UtxoMapper.INSTANCE::modelToResponse).toList())
                .createdAt(model.getCreatedAt())
                .signature(model.getSignature().value())
                .status(model.getStatus())
                .build();

        // When
        TransactionResponse actual = TransactionMapper.INSTANCE.modelToResponse(model);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testTransferModelToRecord() {
        // Given
        TransferModel model = TransactionTestFactory.getTransferModel();

        TransactionRecord expected = new TransactionRecord(
                null,
                model.getHashId().value(),
                model.getSenderAddress().value(),
                model.getRecipientAddress().value(),
                model.getSenderPublicKeyEncoded(),
                model.getAmount().value(),
                model.getFee().value(),
                model.getInputIds(),
                model.getType().name(),
                model.getCreatedAt(),
                model.getSignature().value()
        );

        // When
        TransactionRecord actual = TransactionMapper.INSTANCE.modelToRecord(model);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testRewardModelToRecord() {
        // Given
        RewardModel model = TransactionTestFactory.getRewardModel();

        TransactionRecord expected = new TransactionRecord(
                null,
                model.getHashId().value(),
                model.getSenderAddress().value(),
                model.getRecipientAddress().value(),
                model.getSenderPublicKeyEncoded(),
                model.getAmount().value(),
                model.getFee().value(),
                model.getInputIds(),
                model.getType().name(),
                model.getCreatedAt(),
                model.getSignature().value()
        );

        // When
        TransactionRecord actual = TransactionMapper.INSTANCE.modelToRecord(model);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testRecordToTransferModel() {
        // Given
        TransactionRecord record = TransactionTestFactory.getTransferRecord();

        // Inputs and outputs are not mapped
        TransferModel expected = TransferModel.builder()
                .hashId(new TransactionHash(record.getHashId()))
                .senderAddress(new Address(record.getSenderAddress()))
                .recipientAddress(new Address(record.getRecipientAddress()))
                .senderPublicKeyEncoded(record.getSenderPublicKeyEncoded())
                .amount(new Coin(record.getAmount()))
                .fee(new Coin(record.getFee()))
                .type(TransactionType.TRANSFER)
                .inputs(null)
                .outputs(null)
                .createdAt(record.getCreatedAt())
                .signature(new TransactionSignature(record.getSignature()))
                .status(TransactionStatus.MINED)
                .build();

        // When
        TransactionModel actual = TransactionMapper.INSTANCE.recordToTransferModel(record);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testRecordToRewardModel() {
        // Given
        TransactionRecord record = TransactionTestFactory.getRewardRecord();

        // Outputs are not mapped
        RewardModel expected = RewardModel.builder()
                .hashId(new TransactionHash(record.getHashId()))
                .senderAddress(new Address(record.getSenderAddress()))
                .recipientAddress(new Address(record.getRecipientAddress()))
                .senderPublicKeyEncoded(record.getSenderPublicKeyEncoded())
                .amount(new Coin(record.getAmount()))
                .fee(new Coin(record.getFee()))
                .type(TransactionType.REWARD)
                .inputs(null)
                .outputs(null)
                .createdAt(record.getCreatedAt())
                .signature(new TransactionSignature(record.getSignature()))
                .status(TransactionStatus.MINED)
                .build();

        // When
        RewardModel actual = TransactionMapper.INSTANCE.recordToRewardModel(record);

        // Then
        Assertions.assertEquals(expected, actual);
    }
}
