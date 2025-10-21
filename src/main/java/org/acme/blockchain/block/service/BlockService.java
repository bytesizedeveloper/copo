package org.acme.blockchain.block.service;

import org.acme.blockchain.block.model.BlockModel;
import org.acme.blockchain.block.repository.BlockRepository;
import org.acme.blockchain.common.utility.HashUtility;
import org.acme.blockchain.common.utility.TimestampUtility;
import org.acme.blockchain.transaction.model.CoinModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.exception.NoDataFoundException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Service layer responsible for managing the state of the blockchain,
 * primarily handling block retrieval, persistence, and the creation of the
 * foundational Genesis Block.
 * <p>
 * This {@code @ApplicationScoped} bean acts as the primary interface between
 * the mining and validation logic and the persistent data store
 * (represented by {@link BlockRepository}).
 */
@ApplicationScoped
public class BlockService {

    private final BlockRepository blockRepository;

    /**
     * Constructs the BlockService, injecting the required repository dependency.
     *
     * @param blockRepository The data access object for block persistence operations.
     */
    @Inject
    public BlockService(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    /**
     * Retrieves the latest confirmed block from the persistent blockchain storage.
     * <p>
     * If the repository returns a {@link NoDataFoundException} (i.e., the chain
     * is empty), this method triggers the creation and persistence of the
     * Genesis Block.
     *
     * @return The latest block in the chain, or the newly created Genesis Block.
     */
    public BlockModel getLatestBlock() {
        try {
            return blockRepository.getLatestBlock();
        } catch (NoDataFoundException e) {
            return createGenesisBlock();
        }
    }

    /**
     * Creates, hashes, and persists the very first block in the blockchain
     * (the Genesis Block).
     * <p>
     * The Genesis Block is always given a predetermined state:
     * <ul>
     * <li>{@code previousHashId}: "0"</li>
     * <li>{@code transactions}: Empty list</li>
     * <li>{@code difficulty}: 0</li>
     * <li>{@code reward}: Zero value</li>
     * <li>The block is solved instantly (Nonce 0, no PoW required).</li>
     * </ul>
     *
     * @return The fully initialized and persisted Genesis Block Model.
     */
    private BlockModel createGenesisBlock() {
        OffsetDateTime now = TimestampUtility.getOffsetDateTimeNow();
        long nonce = 0;

        BlockModel genesisBlock = BlockModel.builder()
                .previousHashId("0")
                .transactions(List.of())
                .nonce(nonce)
                .difficulty(0)
                .reward(new CoinModel(BigDecimal.ZERO))
                .createdAt(now)
                .minedAt(now)
                .build();

        genesisBlock.setHashId(HashUtility.calculateSHA256d(genesisBlock.getData() + nonce));

        blockRepository.insert(genesisBlock);

        return genesisBlock;
    }
}
