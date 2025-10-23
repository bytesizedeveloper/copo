package org.acme.blockchain.wallet.service;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.acme.blockchain.common.exception.CryptographicException;
import org.acme.blockchain.common.exception.KeystoreException;
import org.acme.blockchain.wallet.utility.KeyPairUtility;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * A dedicated service for secure persistence and retrieval of
 * cryptographic keys using a password-protected KeyStore file (PKCS12 format).
 * <p>
 * This service obtains the keystore file path and the keystore's master password from
 * Quarkus configuration, ensuring sensitive data is managed securely by accessing and
 * clearing the password immediately for each operation.
 */
@Slf4j
@ApplicationScoped
public class KeystoreService {

    private static final String KEY_STORE_TYPE = "PKCS12";

    @ConfigProperty(name = "copo.keystore.path")
    private String path;

    @ConfigProperty(name = "copo.keystore.password")
    private Supplier<String> passwordSupplier;

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
     * @param alias    The unique alias (name) for the key entry in the keystore (the wallet address).
     * @throws KeystoreException if any file I/O, security, or keystore operation fails.
     * @throws CryptographicException if the keystore algorithm is unavailable.
     */
    public void writePrivateKeyToKeystore(KeyPair keyPair, String alias) {
        char[] password = passwordSupplier.get().toCharArray();

        Path path = Paths.get(this.path);

        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);

            if (Files.exists(path)) {
                try (FileInputStream fis = new FileInputStream(path.toFile())) {
                    log.debug("Existing keystore loaded: {}", this.path);
                    keyStore.load(fis, password);
                }
            } else {
                log.debug("Created new keystore: {}", this.path);
                keyStore.load(null, password);
            }

            X509Certificate certificate = KeyPairUtility.selfSign(keyPair, alias);

            keyStore.setKeyEntry(alias, keyPair.getPrivate(), password, new X509Certificate[]{certificate});

            try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
                keyStore.store(fos, password);
                log.debug("Keystore successfully stored alias: {}", alias);
            }
        } catch (KeyStoreException | CertificateException | OperatorCreationException | IOException e) {
            throw new KeystoreException("Failed to write data to keystore (" + this.path + ") for alias '" + alias +
                    "' due to " + e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptographicException("Failed to write data to keystore (" + this.path + ") for alias '" + alias +
                    "' due to " + KEY_STORE_TYPE + " algorithm unexpectedly unavailable at runtime.", e);
        } finally {
            Arrays.fill(password, ' ');
        }
    }

    /**
     * Reads a private key from the password-protected keystore file.
     * <p>
     * The master password is retrieved from the {@code passwordSupplier}, converted to a
     * transient {@code char[]} copy, and is **securely cleared** in the {@code finally} block.
     *
     * @param alias    The alias of the key entry to retrieve.
     * @return The {@link PrivateKey} retrieved from the keystore.
     * @throws KeystoreException if the keystore is not found; if the alias is not found within the keystore; if the password is incorrect; or any file I/O, security, or keystore operation fails.
     * @throws CryptographicException if the keystore algorithm is unavailable.
     */
    public PrivateKey readPrivateKeyFromKeystore(String alias) {
        char[] password = passwordSupplier.get().toCharArray();

        Path path = Paths.get(this.path);

        if (!Files.exists(path)) {
            String message = "Failed to read data from keystore (" + this.path + ") for alias '" + alias +
                    "' due to keystore file not being found.";
            log.error(message);
            throw new KeystoreException(message);
        }

        try {
            KeyStore keystore = KeyStore.getInstance(KEY_STORE_TYPE);

            try (FileInputStream fis = new FileInputStream(path.toFile())) {
                keystore.load(fis, password);
            }

            PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, password);

            if (privateKey == null) {
                String message = "Failed to read data from keystore (" + this.path + ") for alias '" + alias +
                        "' due to keystore entry not not found for alias.";
                log.error(message);
                throw new KeystoreException(message);
            }

            log.debug("Keystore successfully loaded alias: {}", alias);
            return privateKey;

        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException e) {
            String message = "Failed to read data from keystore (" + this.path + ") for alias '" + alias +
                    "' due to " + e.getMessage();
            log.error(message);
            throw new KeystoreException(message, e);

        } catch (NoSuchAlgorithmException e) {
            String message = "Failed to read data from keystore (" + this.path + ") for alias '" + alias +
                    "' due to " + KEY_STORE_TYPE + " algorithm unexpectedly unavailable at runtime.";
            log.error(message);
            throw new CryptographicException(message, e);

        } finally {
            Arrays.fill(password, ' ');
        }
    }
}
