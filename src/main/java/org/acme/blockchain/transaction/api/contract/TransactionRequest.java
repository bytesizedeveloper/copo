package org.acme.blockchain.transaction.api.contract;

import org.acme.blockchain.common.utility.WalletUtility;
import org.acme.blockchain.transaction.model.CoinModel;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) representing a request from a client to initiate a new fund transfer transaction.
 * <p>
 * This class uses Jakarta Bean Validation annotations to enforce strict structural and quantitative rules
 * on the input data before it is processed by the service layer.
 */
@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
@Schema(description = "Data required to initialise a transaction.")
public class TransactionRequest {

    /**
     * The wallet address from which the funds will be debited.
     * Must be a 69-character, non-blank string in the COPO format.
     */
    @NotBlank(message = "Sender address must not be blank.")
    @Size(min = 69, max = 69, message = "Sender address must be 69 characters in length.")
    @Schema(description = "Wallet address of the sender.", examples = "COPO_abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789")
    @JsonProperty("sender_address")
    private final String senderAddress;

    /**
     * The wallet address to which the transaction amount will be credited (as a new UTXO).
     * Must be a 69-character, non-blank string in the COPO format.
     */
    @NotBlank(message = "Recipient address must not be blank.")
    @Size(min = 69, max = 69, message = "Recipient address must be 69 characters in length.")
    @Schema(description = "Wallet address of the recipient.", examples = "COPO_abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789")
    @JsonProperty("recipient_address")
    private final String recipientAddress;

    /**
     * The quantity of cryptocurrency to transfer.
     * Must be positive, adhere to strict precision rules, and exceed defined minimum bounds.
     * Excluded from default {@code equals}/{@code hashCode} to use the custom, precise comparison method.
     */
    @EqualsAndHashCode.Exclude
    @Digits(integer = 9, fraction = 8, message = "Amount must not exceed 9 integral or 8 fractional digits.")
    @DecimalMin(value = "0.00000001", message = "Amount must exceed or be equal to 0.00000001.")
    @Schema(description = "Quantity of COPO to transfer.", examples = "1000")
    @JsonProperty("amount")
    private final BigDecimal amount;

    /**
     * Constructor annotated for Jackson deserialization, ensuring fields are correctly mapped from JSON keys.
     *
     * @param senderAddress The wallet address of the sender.
     * @param recipientAddress The wallet address of the recipient.
     * @param amount The quantity of cryptocurrency to transfer.
     */
    @JsonCreator
    public TransactionRequest(
            @JsonProperty("sender_address") String senderAddress,
            @JsonProperty("recipient_address") String recipientAddress,
            @JsonProperty("amount") BigDecimal amount
    ) {
        this.senderAddress = senderAddress;
        this.recipientAddress = recipientAddress;
        this.amount = amount;
    }

    /**
     * Provides a short, human-readable summary of the request for logging and debugging purposes.
     *
     * @return A formatted string showing truncated addresses and the amount.
     */
    @Override
    public String toString() {
        String format = """
                [NOT_CALCULATED] | %s -> %s : %s #""";
        return format.formatted(
                this.senderAddress.substring(0, 21),
                this.recipientAddress.substring(0, 21),
                CoinModel.FORMAT.format(this.amount)
        );
    }

    /**
     * Validation method to ensure the sender's address adheres to the cryptographic format (e.g., "COPO_...").
     *
     * @return {@code true} if the format is valid.
     */
    @JsonIgnore
    @AssertTrue(message = "Sender address must be valid format.")
    private boolean isSendersAddressValidFormat() {
        return addressIsValidFormat(this.senderAddress);
    }

    /**
     * Validation method to ensure the recipient's address adheres to the cryptographic format (e.g., "COPO_...").
     *
     * @return {@code true} if the format is valid.
     */
    @JsonIgnore
    @AssertTrue(message = "Recipient address must be valid format.")
    private boolean isRecipientsAddressValidFormat() {
        return addressIsValidFormat(this.recipientAddress);
    }

    /**
     * Validation method to prevent a transaction from sending funds back to the exact same address
     * (self-sends are usually redundant or indicative of an error in the front-end logic).
     *
     * @return {@code true} if the sender and recipient addresses are NOT equal.
     */
    @JsonIgnore
    @AssertTrue(message = "Recipient address must not equal sender address.")
    private boolean isAddressesEqual() {
        return !Objects.equals(this.senderAddress, this.recipientAddress);
    }

    /**
     * Helper method to delegate actual address format validation to the utility class.
     *
     * @param address The address string to check.
     * @return {@code true} if the address is valid.
     */
    private boolean addressIsValidFormat(String address) {
        return WalletUtility.isValid(address);
    }

    /**
     * Custom comparison method for the {@code amount} field used by {@code equals} and {@code hashCode}.
     * Ensures reliable {@code BigDecimal} comparison by stripping trailing zeros.
     *
     * @return The amount stripped of trailing zeros, or null.
     */
    @JsonIgnore
    @EqualsAndHashCode.Include
    private BigDecimal getAmountForEquals() {
        return this.amount != null ? this.amount.stripTrailingZeros() : null;
    }
}
