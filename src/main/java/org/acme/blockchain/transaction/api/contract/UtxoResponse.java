package org.acme.blockchain.transaction.api.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder(toBuilder = true)
@Schema(description = "Information of a transaction output (UTXO).")
public record UtxoResponse(

        @JsonProperty("transaction_hash_id")
        @Schema(description = "Hash ID of parent transaction", examples = "abcdaf0123456789abcdef0123456789abcdef0123456789abcdef0123456789")
        String transactionHashId,

        @JsonProperty("output_index")
        @Schema(description = "Index of output within the parent transaction", examples = {"00", "01"})
        String outputIndex,

        @JsonProperty("recipient_address")
        @Schema(description = "Address of the recipient's wallet", examples = "COPO_0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef")
        String recipientAddress,

        @JsonProperty("amount")
        @Schema(description = "Quantity of COPO to transfer to recipient's wallet", examples = "100")
        BigDecimal amount,

        @JsonProperty("created_at")
        @Schema(description = "Timestamp at which the transaction output was created")
        OffsetDateTime createdAt,

        @JsonProperty("is_spent")
        @Schema(description = "Flag to indicate if the transaction output is spent")
        boolean isSpent
) {}
