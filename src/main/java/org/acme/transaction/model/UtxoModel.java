package org.acme.transaction.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Represents an Unspent Transaction Output (UTXO), the fundamental unit of value
 * and accounting in the COPO blockchain.
 * <p>
 * A UTXO is a record created by a transaction that denotes a certain amount of currency
 * designated for a specific recipient address. Once created, it can only be consumed
 * (marked as spent) as an input in a future transaction.
 */
@Data
@Builder(toBuilder = true)
public class UtxoModel {

    /**
     * Primary key of the UTXO record in the local database. Not part of the blockchain state.
     */
    private Long id;

    /**
     * The hash ID of the transaction that created this output.
     */
    private String transactionHashId;

    /**
     * The index (position) of this output within its parent transaction (e.g., "00", "01").
     * Together with {@code transactionHashId}, this forms a unique UTXO identifier.
     */
    private String outputIndex;

    /**
     * The address that is authorized to spend this output.
     */
    private String recipientAddress;

    /**
     * The monetary value contained within this output. Excluded from {@code equals}/{@code hashCode}
     * by default to use the custom, precise comparison method {@code getAmountForEquals()}.
     */
    @EqualsAndHashCode.Exclude
    private BigDecimal amount;

    /**
     * The timestamp when the parent transaction was created. Excluded from {@code equals}/{@code hashCode}
     * by default to use the custom, truncated comparison method {@code getCreatedAtForEquals()}.
     */
    @EqualsAndHashCode.Exclude
    private OffsetDateTime createdAt;

    /**
     * Flag indicating whether this UTXO has been consumed (spent) as an input in a subsequent transaction.
     * {@code true} means the value is no longer available.
     */
    private boolean isSpent;

    /**
     * Generates a globally unique identifier for this UTXO by concatenating the parent transaction's
     * hash ID and the output index.
     *
     * @return A unique string identifier in the format "TxHashID:OutputIndex".
     */
    public String getUtxoId() {
        return transactionHashId + ":" + outputIndex;
    }

    /**
     * Concatenates the core fields of the UTXO to create a string of immutable data.
     * <p>
     * This string represents the unhashed data that defines the UTXO's identity and is
     * typically used in cryptographic operations or hash calculations.
     *
     * @return A concatenated string of critical UTXO data.
     */
    public String getData() {
        return this.outputIndex +
                this.recipientAddress +
                this.amount +
                this.createdAt;
    }

    /**
     * Provides a short, human-readable summary of the UTXO for logging purposes.
     *
     * @return A formatted string showing the transaction, recipient, and amount.
     */
    @Override
    public String toString() {
        String format = """
                %s:%s | %s : %f""";
        return format.formatted(
                this.transactionHashId != null ? this.transactionHashId.substring(0, 21) : "[NOT CALCULATED]",
                this.outputIndex,
                this.recipientAddress.substring(0, 21),
                this.amount);
    }

    /**
     * Custom comparison method for the {@code amount} field used by {@code equals} and {@code hashCode}.
     * <p>
     * Ensures that BigDecimal comparisons are reliable by stripping trailing zeros, treating
     * e.g., {@code 10.00} and {@code 10} as equal.
     *
     * @return The amount stripped of trailing zeros, or null.
     */
    @EqualsAndHashCode.Include
    private BigDecimal getAmountForEquals() {
        return this.amount != null ? this.amount.stripTrailingZeros() : null;
    }

    /**
     * Custom comparison method for the {@code createdAt} field used by {@code equals} and {@code hashCode}.
     * <p>
     * Truncates the timestamp to millisecond precision to ensure two distinct objects created
     * very close in time (but not exactly the same nanosecond) are considered equal if they are
     * logically the same instance.
     *
     * @return The truncated Instant value, or null.
     */
    @EqualsAndHashCode.Include
    private Instant getCreatedAtForEquals() {
        return this.createdAt != null ? this.createdAt.truncatedTo(ChronoUnit.MILLIS).toInstant() : null;
    }
}
