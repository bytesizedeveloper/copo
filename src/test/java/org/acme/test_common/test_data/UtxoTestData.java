package org.acme.test_common.test_data;

import jooq.tables.records.UtxoRecord;
import org.acme.common.utility.TimestampUtility;
import org.acme.transaction.api.contract.UtxoResponse;
import org.acme.transaction.model.UtxoModel;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class UtxoTestData {

    public static final String TRANSACTION_HASH_ALPHA = "0000000000000000000000000000000000000000000000000000000000000001";

    public static final String TRANSACTION_HASH_BETA = "0000000000000000000000000000000000000000000000000000000000000002";

    public static final String OUTPUT_INDEX_RECIPIENT = "00";

    public static final String OUTPUT_INDEX_SENDER = "01";

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
            .outputIndex(OUTPUT_INDEX_RECIPIENT)
            .recipientAddress(WalletTestData.ADDRESS_ALPHA)
            .amount(BigDecimal.ONE)
            .createdAt(NOW)
            .isSpent(true)
            .build();

    private static final UtxoModel INPUT_UTXO_BETA = UtxoModel.builder()
            .id(2L)
            .transactionHashId(TRANSACTION_HASH_BETA)
            .outputIndex(OUTPUT_INDEX_RECIPIENT)
            .recipientAddress(WalletTestData.ADDRESS_ALPHA)
            .amount(BigDecimal.ONE)
            .createdAt(NOW)
            .isSpent(true)
            .build();

    private static final UtxoModel OUTPUT_UTXO_RECIPIENT = UtxoModel.builder()
            .id(3L)
            .transactionHashId(TransactionTestData.HASH_ID)
            .outputIndex(OUTPUT_INDEX_RECIPIENT)
            .recipientAddress(WalletTestData.ADDRESS_BETA)
            .amount(BigDecimal.ONE)
            .createdAt(NOW)
            .isSpent(false)
            .build();

    private static final UtxoModel OUTPUT_UTXO_SENDER = UtxoModel.builder()
            .id(4L)
            .transactionHashId(TransactionTestData.HASH_ID)
            .outputIndex(OUTPUT_INDEX_SENDER)
            .recipientAddress(WalletTestData.ADDRESS_ALPHA)
            .amount(BigDecimal.ONE)
            .createdAt(NOW)
            .isSpent(false)
            .build();

    private static final UtxoResponse INPUT_RESPONSE_ALPHA = UtxoResponse.builder()
            .transactionHashId(TRANSACTION_HASH_ALPHA)
            .outputIndex(OUTPUT_INDEX_RECIPIENT)
            .recipientAddress(WalletTestData.ADDRESS_ALPHA)
            .amount(BigDecimal.ONE)
            .createdAt(NOW)
            .isSpent(true)
            .build();

    public static final UtxoResponse INPUT_RESPONSE_BETA = UtxoResponse.builder()
            .transactionHashId(TRANSACTION_HASH_BETA)
            .outputIndex(OUTPUT_INDEX_RECIPIENT)
            .recipientAddress(WalletTestData.ADDRESS_ALPHA)
            .amount(BigDecimal.ONE)
            .createdAt(NOW)
            .isSpent(true)
            .build();

    private static final UtxoResponse OUTPUT_RESPONSE_RECIPIENT = UtxoResponse.builder()
            .transactionHashId(TransactionTestData.HASH_ID)
            .outputIndex(OUTPUT_INDEX_RECIPIENT)
            .recipientAddress(WalletTestData.ADDRESS_BETA)
            .amount(BigDecimal.ONE)
            .createdAt(NOW)
            .isSpent(false)
            .build();

    private static final UtxoResponse OUTPUT_RESPONSE_SENDER = UtxoResponse.builder()
            .transactionHashId(TransactionTestData.HASH_ID)
            .outputIndex(OUTPUT_INDEX_SENDER)
            .recipientAddress(WalletTestData.ADDRESS_ALPHA)
            .amount(BigDecimal.ONE)
            .createdAt(NOW)
            .isSpent(false)
            .build();

    private static final UtxoRecord INPUT_RECORD_ALPHA_PRE_INSERT = new UtxoRecord(
            null,
            TRANSACTION_HASH_ALPHA,
            OUTPUT_INDEX_RECIPIENT,
            WalletTestData.ADDRESS_ALPHA,
            BigDecimal.ONE,
            NOW,
            true
    );

    private static final UtxoRecord INPUT_RECORD_ALPHA_POST_INSERT = new UtxoRecord(
            1L,
            TRANSACTION_HASH_ALPHA,
            OUTPUT_INDEX_RECIPIENT,
            WalletTestData.ADDRESS_ALPHA,
            BigDecimal.ONE,
            NOW,
            true
    );
}
