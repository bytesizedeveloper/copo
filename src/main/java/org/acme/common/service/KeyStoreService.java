package org.acme.common.service;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.acme.common.exception.CryptographicException;
import org.acme.common.utility.KeyPairUtility;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * A dedicated, {@link ApplicationScoped} service for secure persistence and retrieval of
 * cryptographic keys using a password-protected KeyStore file (PKCS12 format).
 * <p>
 * This service obtains the keystore file path and the keystore's master password from
 * Quarkus configuration, ensuring sensitive data is managed securely by accessing and
 * clearing the password immediately for each operation.
 */
@Slf4j
@ApplicationScoped
public class KeyStoreService {

    private static final String KEY_STORE_TYPE = "PKCS12";

    /**
     * The file path to the PKCS12 keystore file, injected from the 'copo.keystore.path' configuration property.
     */
    @ConfigProperty(name = "copo.keystore.path")
    private String path;

    /**
     * A {@link Supplier<String>} for the master password used to load and store the keystore file,
     * injected from the 'copo.keystore.password' configuration property.
     * <p>
     * Accessing the password via a Supplier allows the service to retrieve a fresh value
     * for each operation, ensuring the {@code char[]} copy is securely cleared from
     * memory after every use without causing subsequent operations to fail.
     */
    @ConfigProperty(name = "copo.keystore.password")
    Supplier<String> passwordSupplier;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Saves a cryptographic key pair and its self-signed certificate to the keystore file.
     * <p>
     * The master password is retrieved from the {@code passwordSupplier}, converted to a
     * transient {@code char[]} copy, and is **securely cleared** in the {@code finally} block.
     *
     * @param keyPair  The key pair to save (contains public and private keys).
     * @param alias    The unique alias (name) for the key entry in the keystore, typically the wallet address.
     * @throws CryptographicException if any file I/O, security, or keystore operation fails.
     */
    public void writePrivateKeyToKeyStore(KeyPair keyPair, String alias) {
        char[] password = passwordSupplier.get().toCharArray();

        Path path = Paths.get(this.path);

        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);

            // Load existing keystore or initialize new one
            if (Files.exists(path)) {
                try (FileInputStream fis = new FileInputStream(path.toFile())) {
                    log.debug("Existing keystore loaded {}", this.path);
                    keyStore.load(fis, password);
                }
            } else {
                log.debug("New keystore loaded {}", this.path);
                keyStore.load(null, password); // Initialize new keystore
            }

            // Create and set the key entry
            X509Certificate certificate = KeyPairUtility.selfSign(keyPair, alias);

            // The password is used here to protect the *individual key entry*
            keyStore.setKeyEntry(alias, keyPair.getPrivate(), password, new X509Certificate[]{certificate});

            // Store the updated keystore to file
            try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
                keyStore.store(fos, password);
                log.info("Keystore successfully saved / updated at {}", this.path);
            }
        } catch (Exception e) {
            log.error("Failed to write private key entry '{}' to key store: {}", alias, this.path, e);
            throw new CryptographicException("Failed to write private key to key store.", e);
        } finally {
            // Clear the transient password array copy
            Arrays.fill(password, ' ');
        }
    }

    /**
     * Reads a private key from the password-protected keystore file.
     * <p>
     * The master password is retrieved from the {@code passwordSupplier}, converted to a
     * transient {@code char[]} copy, and is **securely cleared** in the {@code finally} block.
     * The method no longer requires a password parameter, relying entirely on configuration.
     *
     * @param alias    The alias of the key entry to retrieve.
     * @return The {@link PrivateKey} retrieved from the keystore.
     * @throws CryptographicException if the keystore file is not found, or any security/I/O operation fails.
     * @throws KeyStoreException if the key entry for the given alias is not found in the keystore.
     */
    public PrivateKey readPrivateKeyFromKeyStore(String alias) throws KeyStoreException {
        char[] password = passwordSupplier.get().toCharArray();

        Path path = Paths.get(this.path);

        if (!Files.exists(path)) {
            String message = "Keystore file not found at: " + this.path;
            log.error(message);
            throw new CryptographicException(message);
        }

        try {
            KeyStore keystore = KeyStore.getInstance(KEY_STORE_TYPE);

            // Load the keystore file using the password
            try (FileInputStream fis = new FileInputStream(path.toFile())) {
                keystore.load(fis, password);
            }

            // Retrieve the private key using the password
            PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, password);

            if (privateKey == null) {
                throw new KeyStoreException("Key entry not found for alias: " + alias);
            }

            return privateKey;

        } catch (KeyStoreException e) {
            // Re-throw KeyStoreException directly as it is part of the method signature
            throw e;
        } catch (Exception e) {
            log.error("Failed to read private key from key store {}", this.path, e);
            throw new CryptographicException("Failed to read private key from key store.", e);
        } finally {
            // Clear the transient password array copy
            Arrays.fill(password, ' ');
        }
    }
}
