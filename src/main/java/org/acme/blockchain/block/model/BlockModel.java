package org.acme.blockchain.block.model;

import org.acme.blockchain.common.utility.MerkleTreeUtility;
import org.acme.blockchain.transaction.model.CoinModel;
import org.acme.blockchain.transaction.model.TransactionModel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder(toBuilder = true)
public class BlockModel {

    private long id;

    private String hashId;

    private String previousHashId;

    private List<TransactionModel> transactions;

    private long height;

    private long nonce;

    private int difficulty;

    private CoinModel reward;

    @EqualsAndHashCode.Exclude
    private OffsetDateTime createdAt;

    @EqualsAndHashCode.Exclude
    private OffsetDateTime minedAt;

    public String getData() {
        return this.previousHashId +
                getMerkleRoot() +
                this.height +
                this.difficulty +
                this.reward.value() +
                this.createdAt;
    }

    public void addTransaction(TransactionModel transaction) {
        this.transactions.add(transaction);
    }

    public long getTimeToMine() {
        Duration duration = Duration.between(createdAt, minedAt);
        return duration.toMillis();
    }

    private String getMerkleRoot() {
        List<String> hashIds = this.transactions.stream()
                .map(TransactionModel::getHashId).collect(Collectors.toCollection(ArrayList::new));
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
