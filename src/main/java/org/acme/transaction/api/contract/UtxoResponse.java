package org.acme.transaction.api.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Data Transfer Object (DTO) representing the immutable information of a single
 * Unspent Transaction Output (UTXO) for API consumers.
 * <p>
 * This model is used to display the inputs and outputs within a {@code TransactionResponse}.
 */
@EqualsAndHashCode
@Builder(toBuilder = true)
@Schema(description = "Information of a transaction output (UTXO).")
public class UtxoResponse {

    /**
     * The hash ID of the transaction that created this output.
     */
    @Schema(description = "Hash ID of parent transaction.")
    @JsonProperty("transaction_hash_id")
    private String transactionHashId;

    /**
     * The index (position) of this output within its parent transaction (e.g., "00", "01").
     */
    @Schema(description = "Index of output within the parent transaction.")
    @JsonProperty("output_index")
    private String outputIndex;

    /**
     * The wallet address that is authorized to spend the value of this output.
     */
    @Schema(description = "Wallet address of the recipient.")
    @JsonProperty("recipient_address")
    private String recipientAddress;

    /**
     * The monetary value contained within this output.
     * Excluded from default {@code equals}/{@code hashCode} to use the custom precision comparison.
     */
    @EqualsAndHashCode.Exclude
    @Schema(description = "Quantity of COPO contained in the output.")
    @JsonProperty("amount")
    private BigDecimal amount;

    /**
     * The timestamp when this UTXO was created (when the parent transaction was created).
     * Excluded from default {@code equals}/{@code hashCode} to use the custom truncated comparison.
     */
    @EqualsAndHashCode.Exclude
    @Schema(description = "Timestamp at which the transaction output was created.")
    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    /**
     * Flag indicating whether this UTXO has been consumed (spent) by a subsequent transaction.
     */
    @Schema(description = "Flag to indicate if the transaction output is spent.")
    @JsonProperty("is_spent")
    private boolean isSpent;

    /**
     * Custom comparison method for the {@code amount} field used by {@code equals} and {@code hashCode}.
     * Ensures that {@code BigDecimal} comparisons are reliable by stripping trailing zeros.
     *
     * @return The amount stripped of trailing zeros, or null.
     */
    @EqualsAndHashCode.Include
    private BigDecimal getAmountForEquals() {
        return this.amount != null ? this.amount.stripTrailingZeros() : null;
    }

    /**
     * Custom comparison method for the {@code createdAt} field used by {@code equals} and {@code hashCode}.
     * Truncates the timestamp to millisecond precision to ensure reliable object comparison.
     *
     * @return The truncated Instant value, or null.
     */
    @EqualsAndHashCode.Include
    private Instant getCreatedAtForEquals() {
        return this.createdAt != null ? this.createdAt.truncatedTo(ChronoUnit.MILLIS).toInstant() : null;
    }
}
