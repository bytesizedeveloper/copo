package org.acme.blockchain.test_common.test_data;

import org.acme.blockchain.transaction.model.CoinModel;
import jooq.tables.records.UtxoRecord;
import org.acme.blockchain.common.utility.TimestampUtility;
import org.acme.blockchain.transaction.api.contract.UtxoResponse;
import org.acme.blockchain.transaction.model.UtxoModel;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class UtxoTestData {

    public static final String TRANSACTION_HASH_ALPHA = "0000000000000000000000000000000000000000000000000000000000000001";

    public static final String TRANSACTION_HASH_BETA = "0000000000000000000000000000000000000000000000000000000000000002";

    public static final CoinModel AMOUNT = new CoinModel(BigDecimal.ONE);

    public static final CoinModel REWARD = new CoinModel(BigDecimal.ONE);

    public static final OffsetDateTime NOW = TimestampUtility.getOffsetDateTimeNow();

    public static UtxoModel getInputUtxoAlpha() {
        return INPUT_UTXO_ALPHA.toBuilder().build();
    }

    public static UtxoModel getInputUtxoBeta() {
        return INPUT_UTXO_BETA.toBuilder().build();
    }

    public static UtxoModel getOutputUtxoRecipient() {
        return OUTPUT_UTXO_RECIPIENT.toBuilder().build();
    }

    public static UtxoModel getOutputUtxoSender() {
        return OUTPUT_UTXO_SENDER.toBuilder().build();
    }

    public static UtxoModel getRewardOutputUtxoSender() {
        return REWARD_OUTPUT_UTXO_SENDER.toBuilder().build();
    }

    public static UtxoResponse getInputResponseAlpha() {
        return INPUT_RESPONSE_ALPHA.toBuilder().build();
    }

    public static UtxoResponse getInputResponseBeta() {
        return INPUT_RESPONSE_BETA.toBuilder().build();
    }

    public static UtxoResponse getOutputResponseRecipient() {
        return OUTPUT_RESPONSE_RECIPIENT.toBuilder().build();
    }

    public static UtxoResponse getOutputResponseSender() {
        return OUTPUT_RESPONSE_SENDER.toBuilder().build();
    }

    public static UtxoRecord getInputRecordAlphaPreInsert() {
        return INPUT_RECORD_ALPHA_PRE_INSERT;
    }

    public static UtxoRecord getInputRecordAlphaPostInsert() {
        return INPUT_RECORD_ALPHA_POST_INSERT;
    }

    private static final UtxoModel INPUT_UTXO_ALPHA = UtxoModel.builder()
            .id(1L)
            .transactionHashId(TRANSACTION_HASH_ALPHA)
            .outputIndex(UtxoModel.OUTPUT_INDEX_RECIPIENT)
            .recipientAddress(WalletTestData.ADDRESS_ALPHA)
            .amount(AMOUNT)
            .createdAt(NOW)
            .isSpent(true)
            .build();

    private static final UtxoModel INPUT_UTXO_BETA = UtxoModel.builder()
            .id(2L)
            .transactionHashId(TRANSACTION_HASH_BETA)
            .outputIndex(UtxoModel.OUTPUT_INDEX_RECIPIENT)
            .recipientAddress(WalletTestData.ADDRESS_ALPHA)
            .amount(AMOUNT)
            .createdAt(NOW)
            .isSpent(true)
            .build();

    private static final UtxoModel OUTPUT_UTXO_RECIPIENT = UtxoModel.builder()
            .id(3L)
            .transactionHashId(TransactionTestData.TRANSACTION_HASH_ID)
            .outputIndex(UtxoModel.OUTPUT_INDEX_RECIPIENT)
            .recipientAddress(WalletTestData.ADDRESS_BETA)
            .amount(AMOUNT)
            .createdAt(NOW)
            .isSpent(false)
            .build();

    private static final UtxoModel OUTPUT_UTXO_SENDER = UtxoModel.builder()
            .id(4L)
            .transactionHashId(TransactionTestData.TRANSACTION_HASH_ID)
            .outputIndex(UtxoModel.OUTPUT_INDEX_SENDER)
            .recipientAddress(WalletTestData.ADDRESS_ALPHA)
            .amount(AMOUNT)
            .createdAt(NOW)
            .isSpent(false)
            .build();

    private static final UtxoModel REWARD_OUTPUT_UTXO_SENDER = UtxoModel.builder()
            .id(5L)
            .transactionHashId(TransactionTestData.ALPHA_REWARD_HASH_ID)
            .outputIndex(UtxoModel.OUTPUT_INDEX_RECIPIENT)
            .recipientAddress(WalletTestData.ADDRESS_ALPHA)
            .amount(REWARD)
            .createdAt(NOW)
            .isSpent(false)
            .build();

    private static final UtxoResponse INPUT_RESPONSE_ALPHA = UtxoResponse.builder()
            .transactionHashId(TRANSACTION_HASH_ALPHA)
            .outputIndex(UtxoModel.OUTPUT_INDEX_RECIPIENT)
            .recipientAddress(WalletTestData.ADDRESS_ALPHA)
            .amount(BigDecimal.ONE)
            .createdAt(NOW)
            .isSpent(true)
            .build();

    public static final UtxoResponse INPUT_RESPONSE_BETA = UtxoResponse.builder()
            .transactionHashId(TRANSACTION_HASH_BETA)
            .outputIndex(UtxoModel.OUTPUT_INDEX_RECIPIENT)
            .recipientAddress(WalletTestData.ADDRESS_ALPHA)
            .amount(BigDecimal.ONE)
            .createdAt(NOW)
            .isSpent(true)
            .build();

    private static final UtxoResponse OUTPUT_RESPONSE_RECIPIENT = UtxoResponse.builder()
            .transactionHashId(TransactionTestData.TRANSACTION_HASH_ID)
            .outputIndex(UtxoModel.OUTPUT_INDEX_RECIPIENT)
            .recipientAddress(WalletTestData.ADDRESS_BETA)
            .amount(BigDecimal.ONE)
            .createdAt(NOW)
            .isSpent(false)
            .build();

    private static final UtxoResponse OUTPUT_RESPONSE_SENDER = UtxoResponse.builder()
            .transactionHashId(TransactionTestData.TRANSACTION_HASH_ID)
            .outputIndex(UtxoModel.OUTPUT_INDEX_SENDER)
            .recipientAddress(WalletTestData.ADDRESS_ALPHA)
            .amount(BigDecimal.ONE)
            .createdAt(NOW)
            .isSpent(false)
            .build();

    private static final UtxoRecord INPUT_RECORD_ALPHA_PRE_INSERT = new UtxoRecord(
            null,
            TRANSACTION_HASH_ALPHA,
            UtxoModel.OUTPUT_INDEX_RECIPIENT,
            WalletTestData.ADDRESS_ALPHA,
            AMOUNT.value(),
            NOW,
            true
    );

    private static final UtxoRecord INPUT_RECORD_ALPHA_POST_INSERT = new UtxoRecord(
            1L,
            TRANSACTION_HASH_ALPHA,
            UtxoModel.OUTPUT_INDEX_RECIPIENT,
            WalletTestData.ADDRESS_ALPHA,
            BigDecimal.ONE,
            NOW,
            true
    );
}
