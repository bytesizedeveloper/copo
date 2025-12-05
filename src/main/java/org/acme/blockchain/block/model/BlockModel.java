package org.acme.blockchain.block.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.acme.blockchain.common.model.Coin;
import org.acme.blockchain.common.utility.MerkleTreeUtility;
import org.acme.blockchain.transaction.model.RewardModel;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransferModel;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder(toBuilder = true)
public class BlockModel {

    private long id;

    private BlockHash hashId;

    private BlockHash previousHashId;

    private LinkedList<TransactionModel> transactions;

    private long height;

    private long nonce;

    private int difficulty;

    private Coin rewardAmount;

    @EqualsAndHashCode.Exclude
    private OffsetDateTime createdAt;

    @EqualsAndHashCode.Exclude
    private OffsetDateTime minedAt;

    public String getData() {
        return this.previousHashId +
                getMerkleRoot() +
                this.height +
                this.difficulty +
                this.rewardAmount.value() +
                this.createdAt;
    }

    public void addReward(RewardModel reward) {
        this.transactions.addFirst(reward);
    }

    public RewardModel getReward() {
        return (RewardModel) this.transactions.getFirst();
    }

    public List<TransferModel> getTransfers() {
        return this.transactions.stream().skip(1).map(transfer -> (TransferModel) transfer).toList();
    }

    public long getTimeToMine() {
        Duration duration = Duration.between(createdAt, minedAt);
        return duration.toMillis();
    }

    private String getMerkleRoot() {
        List<String> hashIds = this.transactions.stream()
                .map(transaction -> transaction.getHashId().value()).collect(Collectors.toCollection(ArrayList::new));
        if (hashIds.isEmpty()) {
            return null;
        }
        return MerkleTreeUtility.calculateMerkleRoot(hashIds);
    }

    @EqualsAndHashCode.Include
    private Instant getCreatedAtForEquals() {
        return this.createdAt != null ? this.createdAt.truncatedTo(ChronoUnit.MILLIS).toInstant() : null;
    }

    @EqualsAndHashCode.Include
    private Instant getMinedAtForEquals() {
        return this.minedAt != null ? this.minedAt.truncatedTo(ChronoUnit.MILLIS).toInstant() : null;
    }
}
