package org.acme.wallet.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.acme.wallet.api.contract.WalletResponse;

import java.security.KeyPair;

/**
 * The core domain model representing a cryptographic wallet in the system.
 * <p>
 * This class stores the essential, immutable components of a wallet: its
 * cryptographic key pair and its public blockchain address.
 */
@Getter
@AllArgsConstructor
public class WalletModel {

    /**
     * The public, human-readable address derived from the public key.
     * This is the identifier used to receive funds on the blockchain.
     */
    private final String address;

    /**
     * The cryptographic key pair (private and public keys) associated with this wallet.
     * This field is kept private and exposed only through specific getters to control access.
     */
    private final KeyPair keyPair;

    /**
     * Retrieves the encoded public key bytes from the key pair.
     * <p>
     * This is typically used for generating signatures or for creating the
     * {@link WalletResponse} object for API exposure.
     *
     * @return The public key in its standard encoded byte format (e.g., X.509).
     */
    public byte[] getPublicKeyEncoded() {
        return this.keyPair.getPublic().getEncoded();
    }
}
