package org.acme.wallet.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.acme.common.service.KeyStoreService;
import org.acme.common.utility.HashUtility;
import org.acme.common.utility.KeyPairUtility;
import org.acme.wallet.model.WalletModel;

import java.security.KeyPair;
import java.security.PublicKey;

/**
 * A service responsible for the creation, derivation, and initial persistence of a cryptocurrency wallet.
 * <p>
 * It leverages {@code KeyPairUtility} for cryptographic key generation and
 * {@code KeyStoreService} for secure storage, abstracting the details of key
 * persistence and configuration-based security.
 */
@Slf4j
@ApplicationScoped
public class WalletService {

    /**
     * Injected service used for securely writing the generated wallet's private key
     * and certificate to the password-protected key store file. The password for the
     * keystore is managed via configuration in the {@code KeyStoreService}.
     */
    @Inject
    private KeyStoreService keyStoreService;

    /**
     * Creates a new wallet by generating a cryptographic key pair, deriving a unique address,
     * and securely persisting the private key to the keystore.
     * <p>
     * This method now obtains the keystore password internally via the injected
     * {@code KeyStoreService}, simplifying the public API.
     *
     * @return A {@link WalletModel} containing the newly generated key pair and derived address.
     * @throws Exception If key generation, address derivation, or keystore persistence fails
     * (e.g., due to an invalid configured password or I/O error).
     */
    public WalletModel create() {
        try {
            KeyPair keyPair = KeyPairUtility.generateKeyPair();
            String address = generateAddress(keyPair.getPublic());

            keyStoreService.writePrivateKeyToKeyStore(keyPair, address);

            log.info("Successfully created and persisted new wallet: {}", address);
            return new WalletModel(address, keyPair);
        } catch (Exception e) {
            log.error("Failed to create and persist new wallet.", e);
            throw e;
        }
    }

    /**
     * Derives a unique cryptocurrency address from a raw public key using a secure
     * double-hashing scheme (SHA-256 followed by BLAKE2b-256).
     *
     * @param publicKey The public key used to identify the wallet owner.
     * @return The formatted wallet address prefixed with "COPO_".
     */
    private String generateAddress(PublicKey publicKey) {
        byte[] publicKeyBytes = publicKey.getEncoded();
        byte[] sha256Hash = HashUtility.calculateSHA256(publicKeyBytes);
        byte[] blake2b256Hash = HashUtility.calculateBLAKE2b256(sha256Hash);

        return "COPO_" + HashUtility.bytesToHex(blake2b256Hash);
    }
}
