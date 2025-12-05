package org.acme.blockchain.test_common.factory;

import jooq.tables.records.UtxoRecord;
import org.acme.blockchain.common.model.Address;
import org.acme.blockchain.common.model.Coin;
import org.acme.blockchain.transaction.api.contract.UtxoResponse;
import org.acme.blockchain.transaction.model.TransactionHash;
import org.acme.blockchain.transaction.model.UtxoId;
import org.acme.blockchain.transaction.model.UtxoModel;
import org.acme.blockchain.transaction.model.enumeration.OutputIndex;
import org.instancio.Instancio;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.instancio.Select.field;

public final class UtxoTestFactory {

    public static UtxoModel getUtxoModel() {
        return Instancio.of(UtxoModel.class)
                .supply(field(UtxoModel::getId), UtxoIdFactory::getUtxoId)
                .supply(field(UtxoModel::getRecipientAddress), AddressTestFactory::getAddress)
                .supply(field(UtxoModel::getAmount), CoinTestFactory::getCoin)
                .create();
    }

    public static UtxoModel getInputUtxoModel(Address address, Coin amount, OffsetDateTime createdAt) {
        return Instancio.of(UtxoModel.class)
                .supply(field(UtxoModel::getId), UtxoIdFactory::getUtxoId)
                .set(field(UtxoModel::getRecipientAddress), address)
                .set(field(UtxoModel::getAmount), amount)
                .supply(field(UtxoModel::getCreatedAt), () -> TimestampTestFactory.generateTimestampBefore(createdAt))
                .set(field(UtxoModel::isSpent), false)
                .create();
    }

    public static UtxoModel getOutputUtxoModel(TransactionHash transactionHash, OutputIndex index, Address address, Coin amount, OffsetDateTime createdAt) {
        return Instancio.of(UtxoModel.class)
                .set(field(UtxoModel::getId), new UtxoId(transactionHash, index))
                .set(field(UtxoModel::getRecipientAddress), address)
                .set(field(UtxoModel::getAmount), amount)
                .set(field(UtxoModel::getCreatedAt), createdAt)
                .set(field(UtxoModel::isSpent), false)
                .create();
    }

    public static UtxoResponse getUtxoResponse() {
        return Instancio.of(UtxoResponse.class)
                .supply(field(UtxoResponse::transactionHashId), TransactionHashTestFactory::getTransactionHashString)
                .supply(field(UtxoResponse::recipientAddress), AddressTestFactory::getAddressString)
                .supply(field(UtxoResponse::amount), CoinTestFactory::getCoinBigDecimal)
                .supply(field(UtxoResponse::createdAt), TimestampTestFactory::generateTimestamp)
                .create();
    }

    public static UtxoResponse getInputUtxoResponse(String address, BigDecimal amount, OffsetDateTime createdAt) {
        return Instancio.of(UtxoResponse.class)
                .supply(field(UtxoResponse::transactionHashId), TransactionHashTestFactory::getTransactionHashString)
                .set(field(UtxoResponse::outputIndex), OutputIndex.RECIPIENT.getIndex())
                .set(field(UtxoResponse::recipientAddress), address)
                .set(field(UtxoResponse::amount), amount)
                .supply(field(UtxoResponse::createdAt), () -> TimestampTestFactory.generateTimestampBefore(createdAt))
                .set(field(UtxoResponse::isSpent), false)
                .create();
    }

    public static UtxoResponse getOutputUtxoResponse(String transactionHash, String index, String address, BigDecimal amount, OffsetDateTime createdAt) {
        return Instancio.of(UtxoResponse.class)
                .set(field(UtxoResponse::transactionHashId), transactionHash)
                .set(field(UtxoResponse::outputIndex), index)
                .set(field(UtxoResponse::recipientAddress), address)
                .set(field(UtxoResponse::amount), amount)
                .set(field(UtxoResponse::createdAt), createdAt)
                .set(field(UtxoResponse::isSpent), false)
                .create();
    }

    public static UtxoRecord getUtxoRecord() {
        return new UtxoRecord(
                Instancio.of(Long.class).create(),
                TransactionHashTestFactory.getTransactionHashString(),
                Instancio.of(OutputIndex.class).create().getIndex(),
                AddressTestFactory.getAddressString(),
                CoinTestFactory.getCoinBigDecimal(),
                Instancio.of(OffsetDateTime.class).create(),
                Instancio.of(boolean.class).create()
        );
    }
}
