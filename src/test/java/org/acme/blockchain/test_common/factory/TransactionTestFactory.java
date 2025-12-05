package org.acme.blockchain.test_common.factory;

import jooq.tables.records.TransactionRecord;
import org.acme.blockchain.common.model.Address;
import org.acme.blockchain.common.model.Coin;
import org.acme.blockchain.transaction.api.contract.TransactionResponse;
import org.acme.blockchain.transaction.api.contract.TransferRequest;
import org.acme.blockchain.transaction.api.contract.UtxoResponse;
import org.acme.blockchain.transaction.model.RewardModel;
import org.acme.blockchain.transaction.model.TransactionHash;
import org.acme.blockchain.transaction.model.TransactionSignature;
import org.acme.blockchain.transaction.model.TransferModel;
import org.acme.blockchain.transaction.model.UtxoModel;
import org.acme.blockchain.transaction.model.enumeration.OutputIndex;
import org.acme.blockchain.transaction.model.enumeration.TransactionType;
import org.instancio.Instancio;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.instancio.Select.field;

public final class TransactionTestFactory {

    public static TransferModel getTransferModelPreInitialise() {
        Address sender = AddressTestFactory.getAddress();
        Address recipient = AddressTestFactory.getAddress();
        Coin amount = CoinTestFactory.getCoin();

        return Instancio.of(TransferModel.class)
                .ignore(field(TransferModel::getHashId))
                .set(field(TransferModel::getSenderAddress), sender)
                .set(field(TransferModel::getRecipientAddress), recipient)
                .set(field(TransferModel::getAmount), amount)
                .ignore(field(TransferModel::getFee))
                .ignore(field(TransferModel::getInputs))
                .ignore(field(TransferModel::getOutputs))
                .ignore(field(TransferModel::getCreatedAt))
                .ignore(field(TransferModel::getSignature))
                .ignore(field(TransferModel::getStatus))
                .create();
    }

    public static TransferModel getTransferModel() {
        TransactionHash transactionHash = TransactionHashTestFactory.getTransactionHash();
        Address sender = AddressTestFactory.getAddress();
        Address recipient = AddressTestFactory.getAddress();
        Coin amount = CoinTestFactory.getCoin();
        Coin fee = CoinTestFactory.getCoin();
        OffsetDateTime createdAt = TimestampTestFactory.generateTimestamp();

        UtxoModel firstInput = UtxoTestFactory.getInputUtxoModel(
                sender,
                new Coin(amount.value().add(BigDecimal.TEN)),
                createdAt
        );
        UtxoModel secondInput = UtxoTestFactory.getInputUtxoModel(
                sender,
                new Coin(fee.value().add(BigDecimal.TEN)),
                createdAt
        );
        List<UtxoModel> inputs = List.of(firstInput, secondInput);

        UtxoModel firstOutput = UtxoTestFactory.getOutputUtxoModel(
                transactionHash,
                OutputIndex.RECIPIENT,
                recipient,
                amount,
                createdAt
        );
        UtxoModel secondOutput = UtxoTestFactory.getOutputUtxoModel(
                transactionHash,
                OutputIndex.SENDER,
                sender,
                new Coin(BigDecimal.valueOf(20)),
                createdAt
        );
        List<UtxoModel> outputs = List.of(firstOutput, secondOutput);

        return Instancio.of(TransferModel.class)
                .set(field(TransferModel::getHashId), transactionHash)
                .set(field(TransferModel::getSenderAddress), sender)
                .set(field(TransferModel::getRecipientAddress), recipient)
                .set(field(TransferModel::getAmount), amount)
                .set(field(TransferModel::getFee), fee)
                .set(field(TransferModel::getType), TransactionType.TRANSFER)
                .set(field(TransferModel::getInputs), inputs)
                .set(field(TransferModel::getOutputs), outputs)
                .set(field(TransferModel::getCreatedAt), createdAt)
                .supply(field(TransferModel::getSignature), TransactionSignatureTestFactory::getTransactionSignature)
                .create();
    }

    public static TransferModel getTransferModel(String inputSender, String inputRecipient, BigDecimal inputAmount) {
        TransactionHash transactionHash = TransactionHashTestFactory.getTransactionHash();
        Address sender = new Address(inputSender);
        Address recipient = new Address(inputRecipient);
        Coin amount = new Coin(inputAmount);
        Coin fee = CoinTestFactory.getCoin();
        OffsetDateTime createdAt = TimestampTestFactory.generateTimestamp();

        UtxoModel firstInput = UtxoTestFactory.getInputUtxoModel(
                sender,
                new Coin(amount.value().add(BigDecimal.TEN)),
                createdAt
        );
        UtxoModel secondInput = UtxoTestFactory.getInputUtxoModel(
                sender,
                new Coin(fee.value().add(BigDecimal.TEN)),
                createdAt
        );
        List<UtxoModel> inputs = List.of(firstInput, secondInput);

        UtxoModel firstOutput = UtxoTestFactory.getOutputUtxoModel(
                transactionHash,
                OutputIndex.RECIPIENT,
                recipient,
                amount,
                createdAt
        );
        UtxoModel secondOutput = UtxoTestFactory.getOutputUtxoModel(
                transactionHash,
                OutputIndex.SENDER,
                sender,
                new Coin(BigDecimal.valueOf(20)),
                createdAt
        );
        List<UtxoModel> outputs = List.of(firstOutput, secondOutput);

        return Instancio.of(TransferModel.class)
                .set(field(TransferModel::getHashId), transactionHash)
                .set(field(TransferModel::getSenderAddress), sender)
                .set(field(TransferModel::getRecipientAddress), recipient)
                .set(field(TransferModel::getAmount), amount)
                .set(field(TransferModel::getFee), fee)
                .set(field(TransferModel::getType), TransactionType.TRANSFER)
                .set(field(TransferModel::getInputs), inputs)
                .set(field(TransferModel::getOutputs), outputs)
                .set(field(TransferModel::getCreatedAt), createdAt)
                .supply(field(TransferModel::getSignature), TransactionSignatureTestFactory::getTransactionSignature)
                .create();
    }

    public static TransferRequest getTransferRequest() {
        return Instancio.of(TransferRequest.class)
                .supply(field(TransferRequest::senderAddress), AddressTestFactory::getAddressString)
                .supply(field(TransferRequest::recipientAddress), AddressTestFactory::getAddressString)
                .supply(field(TransferRequest::amount), CoinTestFactory::getCoinBigDecimal)
                .create();
    }

    public static TransactionResponse getTransferResponse() {
        String transactionHash = TransactionHashTestFactory.getTransactionHashString();
        String sender = AddressTestFactory.getAddressString();
        String recipient = AddressTestFactory.getAddressString();
        BigDecimal amount = CoinTestFactory.getCoinBigDecimal();
        BigDecimal fee = CoinTestFactory.getCoinBigDecimal();
        OffsetDateTime createdAt = TimestampTestFactory.generateTimestamp();

        UtxoResponse firstInput = UtxoTestFactory.getInputUtxoResponse(
                sender,
                amount.add(BigDecimal.TEN),
                createdAt
        );
        UtxoResponse secondInput = UtxoTestFactory.getInputUtxoResponse(
                sender,
                fee.add(BigDecimal.TEN),
                createdAt
        );
        List<UtxoResponse> inputs = List.of(firstInput, secondInput);

        UtxoResponse firstOutput = UtxoTestFactory.getOutputUtxoResponse(
                transactionHash,
                OutputIndex.RECIPIENT.getIndex(),
                recipient,
                amount,
                createdAt
        );
        UtxoResponse secondOutput = UtxoTestFactory.getOutputUtxoResponse(
                transactionHash,
                OutputIndex.SENDER.getIndex(),
                sender,
                BigDecimal.valueOf(20),
                createdAt
        );
        List<UtxoResponse> outputs = List.of(firstOutput, secondOutput);

        return Instancio.of(TransactionResponse.class)
                .set(field(TransactionResponse::hashId), transactionHash)
                .set(field(TransactionResponse::senderAddress), sender)
                .set(field(TransactionResponse::recipientAddress), recipient)
                .set(field(TransactionResponse::amount), amount)
                .set(field(TransactionResponse::fee), fee)
                .set(field(TransactionResponse::type), TransactionType.TRANSFER)
                .set(field(TransactionResponse::inputs), inputs)
                .set(field(TransactionResponse::outputs), outputs)
                .set(field(TransactionResponse::createdAt), createdAt)
                .supply(field(TransactionResponse::signature), TransactionSignatureTestFactory::getTransactionSignatureString)
                .create();
    }

    public static RewardModel getRewardModel() {
        TransactionHash transactionHash = TransactionHashTestFactory.getTransactionHash();
        Address address = AddressTestFactory.getAddress();
        Coin amount = CoinTestFactory.getCoin();
        OffsetDateTime createdAt = TimestampTestFactory.generateTimestamp();

        List<UtxoModel> inputs = List.of();

        UtxoModel output = UtxoTestFactory.getOutputUtxoModel(
                transactionHash,
                OutputIndex.RECIPIENT,
                address,
                amount,
                createdAt
        );
        List<UtxoModel> outputs = List.of(output);

        return Instancio.of(RewardModel.class)
                .set(field(RewardModel::getHashId), transactionHash)
                .set(field(RewardModel::getSenderAddress), address)
                .set(field(RewardModel::getRecipientAddress), address)
                .set(field(RewardModel::getAmount), amount)
                .set(field(RewardModel::getFee), Coin.ZERO)
                .set(field(RewardModel::getType), TransactionType.REWARD)
                .set(field(RewardModel::getInputs), inputs)
                .set(field(RewardModel::getOutputs), outputs)
                .set(field(RewardModel::getCreatedAt), createdAt)
                .set(field(RewardModel::getSignature), TransactionSignature.REWARD_SIGNATURE)
                .create();
    }

    public static TransactionRecord getTransferRecord() {
        return new TransactionRecord(
                Instancio.of(Long.class).create(),
                TransactionHashTestFactory.getTransactionHashString(),
                AddressTestFactory.getAddressString(),
                AddressTestFactory.getAddressString(),
                Instancio.of(byte[].class).create(),
                CoinTestFactory.getCoinBigDecimal(),
                CoinTestFactory.getCoinBigDecimal(),
                Instancio.of(String[].class).create(),
                TransactionType.TRANSFER.getType(),
                TimestampTestFactory.generateTimestamp(),
                TransactionSignatureTestFactory.getTransactionSignatureString()
        );
    }
    public static TransactionRecord getRewardRecord() {
        return new TransactionRecord(
                Instancio.of(Long.class).create(),
                TransactionHashTestFactory.getTransactionHashString(),
                AddressTestFactory.getAddressString(),
                AddressTestFactory.getAddressString(),
                Instancio.of(byte[].class).create(),
                CoinTestFactory.getCoinBigDecimal(),
                Coin.ZERO.value(),
                Instancio.of(String[].class).create(),
                TransactionType.REWARD.getType(),
                TimestampTestFactory.generateTimestamp(),
                TransactionSignatureTestFactory.getTransactionSignatureString()
        );
    }
}
