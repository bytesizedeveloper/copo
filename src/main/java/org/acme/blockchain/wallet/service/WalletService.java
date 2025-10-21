package org.acme.blockchain.wallet.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.acme.blockchain.common.exception.CryptographicException;
import org.acme.blockchain.common.service.KeyStoreService;
import org.acme.blockchain.common.utility.HashUtility;
import org.acme.blockchain.common.utility.KeyPairUtility;
import org.acme.blockchain.common.utility.TimestampUtility;
import org.acme.blockchain.wallet.model.WalletModel;
import org.acme.blockchain.wallet.repository.WalletRepository;
import org.jooq.exception.NoDataFoundException;

import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * A service responsible for the creation, derivation, and initial persistence of a cryptocurrency wallet.
 * It also provides the core function for **cryptographically signing** data using the wallet's private key.
 * <p>
 * It manages the core business logic: quantum-resistant key generation, address derivation,
 * secure private key persistence via {@code KeyStoreService}, and public metadata
 * persistence via {@code WalletRepository}.
 */
@Slf4j
@ApplicationScoped
public class WalletService {

    /**
     * Injected repository for persisting public wallet metadata (address, public key, created timestamp)
     * to the database.
     */
    private final WalletRepository walletRepository;

    /**
     * Injected service used for securely writing and reading the generated wallet's private key
     * and certificate to the password-protected key store file. The password for the
     * keystore is managed via configuration in the {@code KeyStoreService}.
     */
    private final KeyStoreService keyStoreService;

    /**
     * Constructs the WalletService, injecting necessary final dependencies.
     *
     * @param walletRepository The repository for persisting public wallet metadata.
     * @param keyStoreService The service for secure private key storage and retrieval.
     */
    @Inject
    public WalletService(WalletRepository walletRepository, KeyStoreService keyStoreService) {
        this.walletRepository = walletRepository;
        this.keyStoreService = keyStoreService;
    }

    /**
     * Creates a new wallet by generating a cryptographic key pair, deriving a unique address,
     * checking for address collisions, and securely persisting the wallet data.
     *
     * <p>
     * The process involves:
     * <ol>
     * <li>Generating the {@code KeyPair} and deriving the {@code address}.</li>
     * <li>Checking if the generated {@code address} already exists in the database.</li>
     * <li>Persisting the private key using the {@code KeyStoreService}.</li>
     * <li>Saving the public {@code WalletModel} data via the {@code WalletRepository}.</li>
     * </ol>
     *
     * @return A {@link WalletModel} containing the newly generated key pair and derived address.
     * @throws IllegalStateException If the randomly generated address already exists in the database.
     * @throws Exception If key generation, address derivation, or persistence fails
     * (e.g., cryptographic error, database connection failure).
     */
    public WalletModel create() {
        try {
            KeyPair keyPair = KeyPairUtility.generateKeyPair();
            String address = generateAddress(keyPair.getPublic());

            if (walletRepository.exists(address)) {
                log.error("Address collision detected: {}", address);
                throw new IllegalStateException("A wallet with this address already exists. Please retry.");
            }

            keyStoreService.writePrivateKeyToKeyStore(keyPair, address);

            WalletModel wallet = initialise(address, keyPair);

            walletRepository.insert(wallet);

            // Broadcast

            return wallet;
        } catch (Exception e) {
            log.error("Failed to create and persist new wallet", e);
            throw e;
        }
    }

    /**
     * Cryptographically signs a piece of data using the private key associated with the given wallet address,
     * returning the signature as a Hex-encoded string.
     * <p>
     * The private key is retrieved securely from the underlying key store (e.g., accessed via a {@code KeyStoreService})
     * using the address as the alias. The data signed is typically the transaction hash ID, ensuring the integrity
     * and authenticity of the transaction.
     *
     * @param address The wallet address (alias/public key hash) whose corresponding private key should be used for signing.
     * @param data The string representation of the data to be signed (e.g., the transaction hash ID).
     * @return A **Hex-encoded string** representing the digital signature, ready for network transmission or persistence.
     * @throws KeyStoreException If the private key cannot be retrieved from the keystore (e.g., alias not found, wrong password, or corrupt keystore).
     * @throws CryptographicException If the cryptographic signing process fails due to an invalid key, unsupported algorithm, or other internal error.
     */
    public String sign(String address, String data) throws KeyStoreException {
        KeyPair keyPair = getKeyPair(address);
        byte[] signature = KeyPairUtility.sign(keyPair, data);
        return HashUtility.bytesToHex(signature);
    }

    public boolean verifySignature(String address, String originalMessage, String signature) {
        PublicKey publicKey = getPublicKey(address);
        return KeyPairUtility.verifySignature(publicKey, originalMessage, signature);
    }

    public KeyPair getKeyPair(String address) throws KeyStoreException {
        PrivateKey privateKey = keyStoreService.readPrivateKeyFromKeyStore(address);
        PublicKey publicKey = getPublicKey(address);
        return new KeyPair(publicKey, privateKey);
    }

    public PublicKey getPublicKey(String address) {
        try {
            byte[] keyBytes = walletRepository.retrievePublicKeyByAddress(address);
            return KeyPairUtility.loadPublicKey(keyBytes);
        } catch (NoDataFoundException e) {
            log.error("Wallet does not exist: {}", address);
            throw new IllegalStateException("Wallet does not exist.");
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

    /**
     * Creates and initializes a complete {@link WalletModel} domain object
     * with the generated keys and creation timestamp.
     *
     * @param address The derived public wallet address.
     * @param keyPair The generated cryptographic key pair.
     * @return A fully initialized {@link WalletModel} instance.
     */
    private WalletModel initialise(String address, KeyPair keyPair) {
        return WalletModel.builder()
                .address(address)
                .keyPair(keyPair)
                .createdAt(TimestampUtility.getOffsetDateTimeNow())
                .build();
    }
}
