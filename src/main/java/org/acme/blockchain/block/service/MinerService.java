package org.acme.blockchain.block.service;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.acme.blockchain.block.model.BlockHash;
import org.acme.blockchain.block.model.BlockModel;
import org.acme.blockchain.common.model.Address;
import org.acme.blockchain.common.model.Coin;
import org.acme.blockchain.common.service.DifficultyService;
import org.acme.blockchain.common.service.RewardService;
import org.acme.blockchain.common.service.TransferCacheService;
import org.acme.blockchain.common.utility.HashUtility;
import org.acme.blockchain.common.utility.TimestampUtility;
import org.acme.blockchain.network.TempNetwork;
import org.acme.blockchain.transaction.model.RewardModel;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.enumeration.TransactionStatus;
import org.acme.blockchain.transaction.service.TransactionService;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * The core mining service responsible for coordinating the Proof-of-Work (PoW) process.
 * <p>
 * This Quarkus {@code @ApplicationScoped} bean uses the {@code @Scheduled} annotation
 * to periodically wake up and execute mining tasks for all active miners. It manages
 * the lifecycle of a miner (starting/stopping) and orchestrates the creation of new
 * blocks, including transaction inclusion, reward generation, and PoW nonce finding.
 * <p>
 * **Crucially, it uses the {@link MinerCacheService#getIsPulseMined} flag to enable all concurrent**
 * **mining threads to stop immediately upon a successful solve, preventing wasted work.**
 * <p>
 * It relies heavily on injected services for data fetching ({@link MinerCacheService}
 * and {@link TransferCacheService}).
 */
@Slf4j
@ApplicationScoped
public class MinerService {

    private final MinerCacheService minerCache;
    private final TransferCacheService transactionCache;
    private final DifficultyService difficultyService;
    private final RewardService rewardService;
    private final BlockService blockService;
    private final TransactionService transactionService;
    private final TempNetwork tempNetwork;

    /**
     * Constructs the MinerService, injecting all required dependencies.
     * <p>
     * Quarkus automatically handles the injection of these beans.
     *
     * @param minerCache Service to track active mining addresses and the global {@code isMined} status.
     * @param transactionCache Service holding unconfirmed transactions ready for mining.
     * @param difficultyService Service to calculate the current target mining difficulty.
     * @param rewardService Service to determine the current block reward amount.
     * @param blockService Service for retrieving the current blockchain state (latest block).
     * @param transactionService Service for creating special transactions, like the block reward.
     * @param tempNetwork A temporary network utility for broadcasting the newly mined block.
     */
    @Inject
    public MinerService(
            MinerCacheService minerCache,
            TransferCacheService transactionCache,
            DifficultyService difficultyService,
            RewardService rewardService,
            BlockService blockService,
            TransactionService transactionService,
            TempNetwork tempNetwork
    ) {
        this.minerCache = minerCache;
        this.transactionCache = transactionCache;
        this.difficultyService = difficultyService;
        this.rewardService = rewardService;
        this.blockService = blockService;
        this.transactionService = transactionService;
        this.tempNetwork = tempNetwork;
    }

    /**
     * Registers a wallet address as an active miner, allowing it to participate in the PoW pulse.
     *
     * @param address The public wallet address of the entity starting the mining process.
     * @return {@code true} if the miner was successfully added, {@code false} if the address
     * was already mining.
     * @throws IllegalStateException if the provided wallet address is invalid.
     */
    public boolean startMining(Address address) {
        if (minerCache.contains(address)) {
            return false;
        }
        minerCache.add(address);
        log.info("Miner started for address: {}", address);
        return true;
    }

    /**
     * De-registers a wallet address, stopping it from receiving mining tasks in the scheduled pulse.
     *
     * @param address The public wallet address of the entity stopping the mining process.
     * @return {@code true} if the miner was successfully removed, {@code false} if the address
     * was not found in the active miner list.
     * @throws IllegalStateException if the provided wallet address is invalid.
     */
    public boolean stopMining(Address address) {
        if (minerCache.contains(address)) {
            minerCache.remove(address);
            log.info("Miner stopped for address: {}", address);
            return true;
        }
        return false;
    }

    /**
     * The scheduled entry point for the mining process.
     * <p>
     * This method is triggered by the Quarkus scheduler every 10 seconds (as defined by {@code every = "10s"}).
     * It **resets the global {@code isMined} flag to {@code false}** and then prepares a new block template.
     * It finally starts a separate virtual thread for each active miner to attempt the Proof-of-Work.
     */
    @Scheduled(every = "10s")
    public void pulse() {
        Set<Address> activeMiners = minerCache.getIsMining();

        if (activeMiners.isEmpty()) {
            log.info("No active miners. Sleeping for 10 seconds...");
        } else {
            BlockModel toMine = getToMine();
            List<TransactionModel> transactionsToMine = transactionCache.getReadyToMine();

            log.debug("Prepared block template. Difficulty: {}, Reward: {}, Transactions: {}",
                    toMine.getDifficulty(), toMine.getRewardAmount(), transactionsToMine.size());

            log.info("Starting mining pulse with {} active miners on block height {}.",
                    activeMiners.size(), toMine.getHeight());

            minerCache.setIsPulseMined(false);

            for (Address address : activeMiners) {
                Thread.ofVirtual().start(() -> mine(address, toMine.toBuilder()
                        .transactions(new LinkedList<>(transactionsToMine)).build()));
            }
        }
    }

    /**
     * Executes the Proof-of-Work (PoW) process for a single miner on a dedicated thread.
     * <p>
     * This method performs four main steps:
     * 1. Temporarily removes the miner from the cache (for race conditions).
     * 2. Adds the block reward transaction.
     * 3. Calls the PoW loop (which checks the {@code isMined} flag).
     * 4. **If successful, sets {@code isMined} to {@code true}** to signal other threads to stop.
     * 5. Broadcasts the block and re-registers the miner.
     *
     * @param address The wallet address of the miner attempting to find the nonce.
     * @param toMine The block template containing transactions, previous hash, and difficulty.
     */
    private void mine(Address address, BlockModel toMine) {
        log.debug("{} Starting mining process.", address);

        minerCache.remove(address);

        try {
            RewardModel reward = RewardModel.builder()
                    .recipientAddress(address)
                    .amount(toMine.getRewardAmount())
                    .status(TransactionStatus.INITIALISED)
                    .build();

            toMine.addReward(reward);

            BlockModel mined = mineBlockWithProofOfWork(toMine);

            if (mined != null) {
                minerCache.setIsPulseMined(true);

                log.info("{} Block successfully mined in {} milliseconds. Hash: {}",
                        address, mined.getTimeToMine(), mined.getHashId());

                tempNetwork.broadcast(mined);
            } else {
                log.debug("{} PoW attempt terminated early because the block was mined by another peer.", address);
            }

        } catch (Exception e) {
            log.error("{} An error occurred during the PoW mining attempt: {}", address, e.getMessage(), e);
        } finally {
            minerCache.add(address);
        }
    }

    /**
     * Executes the core Proof-of-Work loop by iteratively incrementing the nonce until a valid hash is found.
     * <p>
     * The loop termination condition is twofold:
     * 1. The resulting hash begins with the {@code target} string (success).
     * 2. **The {@link MinerCacheService#getIsPulseMined()} flag is {@code true}** (failure/termination).
     * <p>
     * This allows the mining thread to stop instantly when another successful block is found and broadcast.
     *
     * @param block The block model containing the data and the difficulty target.
     * @return The same block model, either solved (with nonce/hashId/minedAt set) or returned
     * early if the {@code isMined} flag was set by a competing miner.
     */
    private BlockModel mineBlockWithProofOfWork(BlockModel block) {
        String target = "0".repeat(block.getDifficulty());
        long nonce = 0;

        String data = block.getData();
        String hashId;

        do {
            nonce++;
            hashId = HashUtility.calculateSHA256d(data + nonce);
        } while (!hashId.startsWith(target) && !minerCache.getIsPulseMined());

        if (!minerCache.getIsPulseMined()) {
            OffsetDateTime minedAt = TimestampUtility.getOffsetDateTimeNow();

            block.setNonce(nonce);
            block.setHashId(new BlockHash(hashId));
            block.setMinedAt(minedAt);

            return block;
        } else {
            return null;
        }
    }

    /**
     * Assembles the block template that all active miners will attempt to solve.
     * <p>
     * This involves fetching the latest blockchain state, determining the current
     * difficulty, calculating the reward, and gathering transactions from the memory pool.
     *
     * @return A {@link BlockModel} template ready for the Proof-of-Work attempt.
     */
    private BlockModel getToMine() {
        BlockModel latest = blockService.getLatestBlock();
        int difficulty = difficultyService.calculateDifficulty();
        Coin reward = rewardService.determineRewardAmount();

        return initialise(latest, difficulty, reward);
    }

    /**
     * Initialises a new {@link BlockModel} instance based on the current blockchain state and parameters.
     *
     * @param latest The last confirmed block in the chain.
     * @param difficulty The calculated mining difficulty for the new block.
     * @param reward The determined coin reward for the miner.
     * @return A fully initialised, but unsolved, {@link BlockModel} ready for PoW.
     */
    private BlockModel initialise(
            BlockModel latest,
            int difficulty,
            Coin reward
    ) {
        OffsetDateTime now = TimestampUtility.getOffsetDateTimeNow();
        long currentHeight = latest.getHeight() + 1;

        return BlockModel.builder()
                .previousHashId(latest.getHashId())
                .height(currentHeight)
                .difficulty(difficulty)
                .rewardAmount(reward)
                .createdAt(now)
                .build();
    }
}
