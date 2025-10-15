package org.acme.test_common.test_data;

import jooq.tables.records.TransactionRecord;
import org.acme.common.utility.TimestampUtility;
import org.acme.transaction.api.contract.TransactionRequest;
import org.acme.transaction.api.contract.TransactionResponse;
import org.acme.transaction.model.TransactionModel;
import org.acme.transaction.model.enumeration.TransactionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public final class TransactionTestData {

    public static final String HASH_ID = "1000000000000000000000000000000000000000000000000000000000000000";

    public static final OffsetDateTime NOW = TimestampUtility.getOffsetDateTimeNow();

    public static final String SIGNATURE = "SIGNATURE";

    public static TransactionRequest getRequest() {
        return REQUEST.toBuilder().build();
    }

    public static TransactionModel getTransactionPreInitialise() {
        return TRANSACTION_PRE_INITIALISE.toBuilder().build();
    }

    public static TransactionModel getTransactionPostInitialise() {
        return TRANSACTION_POST_INITIALISE.toBuilder().build();
    }

    public static TransactionResponse getResponse() {
        return RESPONSE.toBuilder().build();
    }

    public static TransactionRecord getRecordPreInsert() {
        return RECORD_PRE_INSERT;
    }

    public static TransactionRecord getRecordPostInsert() {
        return RECORD_POST_INSERT;
    }

    private static final TransactionRequest REQUEST = TransactionRequest.builder()
            .senderAddress(WalletTestData.ADDRESS_ALPHA)
            .recipientAddress(WalletTestData.ADDRESS_BETA)
            .amount(BigDecimal.ONE)
            .build();

    private static final TransactionModel TRANSACTION_PRE_INITIALISE = TransactionModel.builder()
            .senderAddress(WalletTestData.ADDRESS_ALPHA)
            .recipientAddress(WalletTestData.ADDRESS_BETA)
            .amount(BigDecimal.ONE)
            .build();

    private static final TransactionModel TRANSACTION_POST_INITIALISE = TransactionModel.builder()
            .id(1L)
            .hashId(HASH_ID)
            .senderAddress(WalletTestData.ADDRESS_ALPHA)
            .recipientAddress(WalletTestData.ADDRESS_BETA)
            .amount(BigDecimal.ONE)
            .fee(BigDecimal.ONE)
            .type(TransactionType.TRANSFER)
            .inputs(List.of(UtxoTestData.getInputUtxoAlpha(), UtxoTestData.getInputUtxoBeta()))
            .outputs(List.of(UtxoTestData.getOutputUtxoRecipient(), UtxoTestData.getOutputUtxoSender()))
            .createdAt(NOW)
            .signature(SIGNATURE)
            .build();

    private static final TransactionResponse RESPONSE = TransactionResponse.builder()
            .hashId(HASH_ID)
            .senderAddress(WalletTestData.ADDRESS_ALPHA)
            .recipientAddress(WalletTestData.ADDRESS_BETA)
            .amount(BigDecimal.ONE)
            .fee(BigDecimal.ONE)
            .type(TransactionType.TRANSFER)
            .inputs(List.of(UtxoTestData.getInputResponseAlpha(), UtxoTestData.getInputResponseBeta()))
            .outputs(List.of(UtxoTestData.getOutputResponseRecipient(), UtxoTestData.getOutputResponseSender()))
            .createdAt(NOW)
            .signature(SIGNATURE)
            .build();

    private static final TransactionRecord RECORD_PRE_INSERT = new TransactionRecord(
            null,
            HASH_ID,
            WalletTestData.ADDRESS_ALPHA,
            WalletTestData.ADDRESS_BETA,
            BigDecimal.ONE,
            BigDecimal.ONE,
            TransactionType.TRANSFER.name(),
            NOW,
            SIGNATURE
    );

    private static final TransactionRecord RECORD_POST_INSERT = new TransactionRecord(
            1L,
            HASH_ID,
            WalletTestData.ADDRESS_ALPHA,
            WalletTestData.ADDRESS_BETA,
            BigDecimal.ONE,
            BigDecimal.ONE,
            TransactionType.TRANSFER.name(),
            NOW,
            SIGNATURE
    );
}
