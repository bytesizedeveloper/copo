package org.acme.blockchain.wallet.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.acme.blockchain.common.exception.CryptographicException;
import org.acme.blockchain.common.exception.KeystoreException;
import org.acme.blockchain.common.model.Address;
import org.acme.blockchain.common.utility.HashUtility;
import org.acme.blockchain.common.utility.TimestampUtility;
import org.acme.blockchain.transaction.model.TransactionSignature;
import org.acme.blockchain.wallet.model.WalletModel;
import org.acme.blockchain.wallet.repository.WalletRepository;
import org.acme.blockchain.wallet.utility.KeyPairUtility;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.NoDataFoundException;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * A service responsible for the creation, derivation, and initial persistence of a cryptocurrency wallet.
 * It also provides the core function for **cryptographically signing** data using the wallet's private key.
 * <p>
 * It manages the core business logic, including:
 * <ul>
 * <li>Quantum-resistant key generation (assumed via {@code KeyPairUtility}).</li>
 * <li>Address derivation using a secure double-hashing scheme.</li>
 * <li>Secure private key persistence via {@code KeyStoreService}.</li>
 * <li>Public wallet metadata persistence via {@code WalletRepository}.</li>
 * </ul>
 */
@ApplicationScoped
public class WalletService {

    private final WalletRepository walletRepository;

    private final KeystoreService keyStoreService;

    @Inject
    public WalletService(WalletRepository walletRepository, KeystoreService keyStoreService) {
        this.walletRepository = walletRepository;
        this.keyStoreService = keyStoreService;
    }

    /**
     * Creates a new wallet by generating a cryptographic key pair, deriving a unique address,
     * checking for address collisions, and securely persisting the wallet data.
     *
     * <p>
     * The wallet creation process involves the following steps:
     * <ol>
     * <li>Generating a quantum-resistant {@code KeyPair} (ML-DSA).</li>
     * <li>Deriving the wallet {@code address} from the Public Key using double-hashing.</li>
     * <li>Checking the {@code WalletRepository} for an existing wallet with the generated address.</li>
     * <li>Persisting the Private Key securely using the {@code KeyStoreService}.</li>
     * <li>Saving the public {@code WalletModel} data via the {@code WalletRepository}.</li>
     * </ol>
     *
     * @return A {@link WalletModel} containing the newly generated address, public key information, and timestamp.
     * @throws IllegalStateException If the randomly generated address already exists in the database, indicating an address collision (extremely rare).
     * @throws CryptographicException If the key pair cannot be generated or any underlying cryptographic algorithm is unavailable.
     * @throws KeystoreException If the private key cannot be securely written to the key store file.
     * @throws DataAccessException If the public wallet metadata cannot be persisted in the database.
     */
    public WalletModel create() {
        KeyPair keyPair = KeyPairUtility.generateKeyPair();
        Address address = generateAddress(keyPair.getPublic());

        if (walletRepository.exists(address.value())) {
            throw new IllegalStateException("A wallet with this address already exists.");
        }

        keyStoreService.writePrivateKeyToKeystore(keyPair, address.value());

        WalletModel wallet = initialise(keyPair, address);
        walletRepository.insert(wallet);

        return wallet;
    }

    /**
     * Retrieves the public wallet metadata (address, public key, timestamp) from the database using the unique wallet address.
     *
     * @param address The {@link Address} containing the unique wallet address to look up.
     * @return The {@link WalletModel} instance.
     * @throws NotFoundException If no wallet exists for the given address in the database (i.e., {@code NoDataFoundException} is caught).
     */
    public WalletModel get(Address address) {
        try {
            return walletRepository.retrieveWalletByAddress(address.value());

        } catch (NoDataFoundException e) {
            throw new NotFoundException("Wallet does not exist in the database: " + address.value());
        }
    }

    /**
     * Cryptographically signs a piece of data using the **private key** associated with the given wallet address.
     * <p>
     * The private key is securely retrieved from the key store using the address as the alias.
     * This method is typically used to sign a transaction hash ID, ensuring the integrity and authenticity of the sender.
     *
     * @param address The {@link Address} acting as the alias to retrieve the private key.
     * @param unsignedMessage The string representation of the data to be signed (e.g., the transaction hash ID).
     * @return A **Hex-encoded string** representing the digital signature.
     * @throws KeystoreException If the private key cannot be read from the key store (e.g., file not found, incorrect password, or alias missing).
     * @throws CryptographicException If the underlying cryptographic signing process fails.
     */
    public TransactionSignature sign(Address address, String unsignedMessage) {
        PrivateKey privateKey = getPrivateKey(address);

        byte[] signature = KeyPairUtility.sign(privateKey, unsignedMessage.getBytes(StandardCharsets.UTF_8));

        return new TransactionSignature(HashUtility.bytesToHex(signature));
    }

    /**
     * Verifies if a given digital signature is valid for the original message and the public key derived from the provided byte array.
     * <p>
     * This process involves loading the raw public key bytes into a {@link PublicKey} object and then executing the verification algorithm.
     *
     * @param keyBytes The encoded **Public Key** bytes to be used for verification.
     * @param originalMessage The original data (e.g., transaction hash) that was signed.
     * @param signature The Hex-encoded digital signature string to verify.
     * @return {@code true} if the signature is valid for the message and public key; {@code false} otherwise.
     * @throws CryptographicException If the public key cannot be loaded or the verification process setup fails.
     */
    public boolean verifySignature(byte[] keyBytes, String originalMessage, TransactionSignature signature) {
        PublicKey publicKey = KeyPairUtility.loadPublicKey(keyBytes);

        return KeyPairUtility.verifySignature(publicKey, originalMessage.getBytes(StandardCharsets.UTF_8), signature.toBytes());
    }

    /**
     * Retrieves the wallet's raw encoded **Public Key** by querying the database using the wallet address.
     *
     * @param address The {@link Address} to look up.
     * @return The raw encoded public key as a {@code byte[]} array.
     * @throws NotFoundException If the wallet and its public key do not exist in the database (i.e., {@code NoDataFoundException} is caught).
     */
    public byte[] getPublicKeyEncoded(Address address) {
        try {
            return walletRepository.retrievePublicKeyByAddress(address.value());
        } catch (NoDataFoundException e) {
            throw new NotFoundException("Wallet does not exist in the database: " + address.value());
        }
    }

    /**
     * Retrieves the **Private Key** associated with the given address by reading it from the secure {@code KeystoreService}.
     *
     * @param address The {@link Address} acting as the alias for key retrieval.
     * @return The {@link PrivateKey} instance.
     * @throws KeystoreException If the private key cannot be read from the key store (e.g., file not found, incorrect password, or alias missing).
     */
    private PrivateKey getPrivateKey(Address address) {
        return keyStoreService.readPrivateKeyFromKeystore(address.value());
    }

    /**
     * Derives a unique COPO address from a raw public key using a secure
     * double-hashing scheme (SHA-256 followed by BLAKE2b-256).
     *
     * @param publicKey The public key used to identify the wallet owner.
     * @return An {@link Address} instance containing the formatted wallet address, prefixed with **"COPO_"**.
     */
    private Address generateAddress(PublicKey publicKey) {
        byte[] publicKeyBytes = publicKey.getEncoded();
        byte[] sha256Hash = HashUtility.calculateSHA256(publicKeyBytes);
        byte[] blake2b256Hash = HashUtility.calculateBLAKE2b256(sha256Hash);

        return new Address(Address.PREFIX + HashUtility.bytesToHex(blake2b256Hash));
    }

    /**
     * Creates and initialises a complete {@link WalletModel} domain object
     * with the generated keys, encoded public key, and the creation timestamp.
     *
     * @param keyPair The generated cryptographic key pair.
     * @param address The derived public wallet address.
     * @return A fully initialised {@link WalletModel} instance.
     */
    private WalletModel initialise(KeyPair keyPair, Address address) {
        return WalletModel.builder()
                .keyPair(keyPair)
                .address(address)
                .publicKeyEncoded(keyPair.getPublic().getEncoded())
                .createdAt(TimestampUtility.getOffsetDateTimeNow())
                .build();
    }
}
