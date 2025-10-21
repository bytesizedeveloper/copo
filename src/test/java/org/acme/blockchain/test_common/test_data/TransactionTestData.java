package org.acme.blockchain.test_common.test_data;

import org.acme.blockchain.transaction.model.CoinModel;
import jooq.tables.records.TransactionRecord;
import org.acme.blockchain.common.utility.TimestampUtility;
import org.acme.blockchain.transaction.api.contract.TransactionRequest;
import org.acme.blockchain.transaction.api.contract.TransactionResponse;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.enumeration.TransactionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public final class TransactionTestData {

    public static final String TRANSACTION_HASH_ID = "1000000000000000000000000000000000000000000000000000000000000000";

    public static final String ALPHA_REWARD_HASH_ID = "2000000000000000000000000000000000000000000000000000000000000000";

    public static final String BETA_REWARD_HASH_ID = "3000000000000000000000000000000000000000000000000000000000000000";

    public static final CoinModel AMOUNT = new CoinModel(BigDecimal.ONE);

    public static final CoinModel REWARD = new CoinModel(BigDecimal.ONE);

    public static final CoinModel FEE = new CoinModel(BigDecimal.ZERO);

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

    public static TransactionModel getAlphaRewardPostInitialise() {
        return ALPHA_REWARD_POST_INITIALISE.toBuilder().build();
    }

    public static TransactionModel getBetaRewardPostInitialise() {
        return BETA_REWARD_POST_INITIALISE.toBuilder().build();
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
            .amount(AMOUNT)
            .build();

    private static final TransactionModel TRANSACTION_POST_INITIALISE = TransactionModel.builder()
            .id(1L)
            .hashId(TRANSACTION_HASH_ID)
            .senderAddress(WalletTestData.ADDRESS_ALPHA)
            .recipientAddress(WalletTestData.ADDRESS_BETA)
            .amount(AMOUNT)
            .fee(FEE)
            .type(TransactionType.TRANSFER)
            .inputs(List.of(UtxoTestData.getInputUtxoAlpha(), UtxoTestData.getInputUtxoBeta()))
            .outputs(List.of(UtxoTestData.getOutputUtxoRecipient(), UtxoTestData.getOutputUtxoSender()))
            .createdAt(NOW)
            .signature(SIGNATURE)
            .build();

    private static final TransactionModel ALPHA_REWARD_POST_INITIALISE = TransactionModel.builder()
            .id(2L)
            .hashId(ALPHA_REWARD_HASH_ID)
            .senderAddress(WalletTestData.ADDRESS_ALPHA)
            .recipientAddress(WalletTestData.ADDRESS_ALPHA)
            .amount(REWARD)
            .fee(new CoinModel(BigDecimal.ZERO))
            .type(TransactionType.REWARD)
            .inputs(List.of())
            .outputs(List.of(UtxoTestData.getRewardOutputUtxoSender()))
            .createdAt(NOW)
            .signature(SIGNATURE)
            .build();

    private static final TransactionModel BETA_REWARD_POST_INITIALISE = TransactionModel.builder()
            .id(2L)
            .hashId(BETA_REWARD_HASH_ID)
            .senderAddress(WalletTestData.ADDRESS_BETA)
            .recipientAddress(WalletTestData.ADDRESS_BETA)
            .amount(REWARD)
            .fee(new CoinModel(BigDecimal.ZERO))
            .type(TransactionType.REWARD)
            .inputs(List.of())
            .outputs(List.of(UtxoTestData.getRewardOutputUtxoSender()))
            .createdAt(NOW)
            .signature(SIGNATURE)
            .build();

    private static final TransactionResponse RESPONSE = TransactionResponse.builder()
            .hashId(TRANSACTION_HASH_ID)
            .senderAddress(WalletTestData.ADDRESS_ALPHA)
            .recipientAddress(WalletTestData.ADDRESS_BETA)
            .amount(BigDecimal.ONE)
            .fee(BigDecimal.ZERO)
            .type(TransactionType.TRANSFER)
            .inputs(List.of(UtxoTestData.getInputResponseAlpha(), UtxoTestData.getInputResponseBeta()))
            .outputs(List.of(UtxoTestData.getOutputResponseRecipient(), UtxoTestData.getOutputResponseSender()))
            .createdAt(NOW)
            .signature(SIGNATURE)
            .build();

    private static final TransactionRecord RECORD_PRE_INSERT = new TransactionRecord(
            null,
            TRANSACTION_HASH_ID,
            WalletTestData.ADDRESS_ALPHA,
            WalletTestData.ADDRESS_BETA,
            AMOUNT.value(),
            FEE.value(),
            TransactionType.TRANSFER.name(),
            NOW,
            SIGNATURE
    );

    private static final TransactionRecord RECORD_POST_INSERT = new TransactionRecord(
            1L,
            TRANSACTION_HASH_ID,
            WalletTestData.ADDRESS_ALPHA,
            WalletTestData.ADDRESS_BETA,
            BigDecimal.ONE,
            BigDecimal.ZERO,
            TransactionType.TRANSFER.name(),
            NOW,
            SIGNATURE
    );
}
