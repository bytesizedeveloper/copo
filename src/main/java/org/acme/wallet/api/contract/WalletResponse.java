package org.acme.wallet.api.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.OffsetDateTime;

/**
 * Data Transfer Object (DTO) used to return the public identifying information
 * of a newly created cryptocurrency wallet to the user.
 * <p>
 * This object contains the public key's raw encoded bytes and the derived
 * public wallet address.
 */
@EqualsAndHashCode
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Public information of a new wallet that has been created.")
public class WalletResponse {

    /**
     * The unique, human-readable address derived from the public key, prefixed with "COPO_".
     * This is the identifier used to receive funds.
     */
    @Getter
    @Schema(description = "Address of the wallet.")
    @JsonProperty("address")
    private final String address;

    /**
     * The public key of the wallet's key pair, encoded in its raw byte format (e.g., X.509/SubjectPublicKeyInfo).
     * This allows clients to verify signatures made by the private key.
     */
    @Schema(description = "Encoded public key of the wallet.")
    @JsonProperty("public_key")
    private final byte[] publicKeyEncoded;

    @Schema(description = "Timestamp at which the wallet was created.")
    @JsonProperty("created_at")
    private final OffsetDateTime createdAt;
}
