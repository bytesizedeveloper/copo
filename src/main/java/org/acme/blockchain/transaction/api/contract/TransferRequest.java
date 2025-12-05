package org.acme.blockchain.transaction.api.contract;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.Builder;
import org.acme.blockchain.common.model.Coin;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Builder(toBuilder = true)
@Schema(description = "Data required to initialise a transfer.")
public record TransferRequest(

        @JsonProperty("sender_address")
        @Schema(description = "Address of the sender's wallet", examples = "COPO_abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789")
        String senderAddress,

        @JsonProperty("recipient_address")
        @Schema(description = "Address of the recipient's wallet", examples = "COPO_0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef")
        String recipientAddress,

        @JsonProperty("amount")
        @Schema(description = "Quantity of COPO to transfer from sender's to recipient's wallet", examples = "100")
        @Digits(integer = 9, fraction = 8, message = "Amount must not exceed 9 integral or 8 fractional digits.")
        @DecimalMin(value = "0.00000001", message = "Amount must exceed or be equal to 0.00000001.")
        BigDecimal amount
) {

    @JsonCreator
    public TransferRequest(
            @JsonProperty("sender_address") String senderAddress,
            @JsonProperty("recipient_address") String recipientAddress,
            @JsonProperty("amount") BigDecimal amount
    ) {
        this.senderAddress = senderAddress;
        this.recipientAddress = recipientAddress;
        this.amount = amount.setScale(Coin.SCALE, RoundingMode.DOWN);
    }

    @JsonIgnore
    @AssertTrue(message = "Recipient address must not equal sender address.")
    private boolean isAddressesEqual() {
        return !Objects.equals(this.senderAddress, this.recipientAddress);
    }
}
