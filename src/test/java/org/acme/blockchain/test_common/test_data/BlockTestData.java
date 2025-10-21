package org.acme.blockchain.test_common.test_data;

import org.acme.blockchain.block.model.BlockModel;
import org.acme.blockchain.common.utility.TimestampUtility;
import org.acme.blockchain.transaction.model.CoinModel;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public final class BlockTestData {

    public static final String VALID_HASH_ID_1 = "01";

    public static final String VALID_HASH_ID_2 = "02";

    public static final String PREVIOUS_HASH_ID = "0";

    public static final OffsetDateTime NOW = TimestampUtility.getOffsetDateTimeNow();

    public static final long NONCE = 0;

    public static final int DIFFICULTY = 1;

    public static BlockModel getBlock() {
        return BLOCK.toBuilder().build();
    }

    public static BlockModel getGenesisBlock() {
        return GENESIS_BLOCK.toBuilder().build();
    }

    private static final BlockModel BLOCK = BlockModel.builder()
            .hashId(VALID_HASH_ID_1)
            .previousHashId(PREVIOUS_HASH_ID)
            .transactions(List.of())
            .nonce(NONCE)
            .difficulty(DIFFICULTY)
            .reward(TransactionTestData.REWARD)
            .createdAt(NOW)
            .minedAt(NOW)
            .build();

    private static final BlockModel GENESIS_BLOCK = BlockModel.builder()
            .previousHashId("0")
            .transactions(List.of())
            .nonce(0)
            .difficulty(0)
            .reward(new CoinModel(BigDecimal.ZERO))
            .build();
}
