package org.acme.blockchain.wallet.utility;

import lombok.extern.slf4j.Slf4j;
import org.acme.blockchain.common.exception.CryptographicException;
import org.acme.blockchain.common.utility.HashUtility;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;

/**
 * A specialized utility class for generating, signing, and verifying cryptographic
 * operations related to the **ML-DSA (Dilithium)** quantum-resistant algorithm.
 * <p>
 * This class ensures thread-safe access to core cryptographic functions by creating
 * a new {@link Signature} instance for each operation, leveraging the Bouncy Castle
 * (BC) provider for Dilithium-specific algorithms and parameter sets (Dilithium 5).
 */
@Slf4j
public final class KeyPairUtility {

    private static final String KEY_PAIR_ALGORITHM = "ML-DSA";

    private static final String BC_PROVIDER = "BC";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Generates a new ML-DSA key pair using the **ml-dsa-87** (Dilithium 5) security level.
     * <p>
     * This parameter set corresponds to **NIST's Level 5 security** for the ML-DSA algorithm.
     *
     * @return A cryptographically secure {@link KeyPair} consisting of a public and private key.
     * @throws CryptographicException if the ML-DSA algorithm or Bouncy Castle provider is not correctly configured or available.
     */
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_PAIR_ALGORITHM, BC_PROVIDER);
            keyGen.initialize(MLDSAParameterSpec.ml_dsa_87);
            return keyGen.generateKeyPair();
        } catch (Exception e) {
            throw new CryptographicException("Failed to generate ML-DSA key pair.", e);
        }
    }

    /**
     * **Convenience Method:** Signs a message string using the provided private key and the ML-DSA algorithm.
     * <p>
     * The input string is converted to bytes using **{@code UTF-8} encoding** before the signing operation is performed.
     *
     * @param privateKey The {@link PrivateKey} used to generate the digital signature.
     * @param unsignedMessage The message {@code String} to sign.
     * @return The digital signature as a raw byte array.
     * @throws CryptographicException if the signing process fails due to an invalid key, unsupported algorithm, or a crypto error.
     */
    public static byte[] sign(PrivateKey privateKey, String unsignedMessage) {
        return sign(privateKey, unsignedMessage.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * **Convenience Method:** Verifies a digital signature given a public key, the original message string, and the signature
     * in its **Hex-encoded string format**.
     * <p>
     * This method handles the decoding of the signature Hex string back to bytes and converts the message string
     * to {@code UTF-8} bytes before calling the core verification method.
     *
     * @param publicKey       The {@link PublicKey} used for signature verification.
     * @param originalMessage The original message {@code String} that was signed.
     * @param signature       The digital signature as a hexadecimal {@code String}.
     * @return {@code true} if the signature is valid for the message; {@code false} otherwise.
     * @throws CryptographicException if the verification setup fails (e.g., invalid key) or if the signature Hex string cannot be decoded (via {@code HashUtility.hexToBytes}).
     */
    public static boolean verifySignature(PublicKey publicKey, String originalMessage, String signature) {
        return verifySignature(publicKey, originalMessage.getBytes(StandardCharsets.UTF_8), HashUtility.hexToBytes(signature));
    }

    /**
     * Loads a {@link PublicKey} instance from its standard X.509 encoded byte array representation.
     * <p>
     * This method is essential for **deserializing ML-DSA public keys** received from the network or loaded from persistent storage.
     * It uses the {@link X509EncodedKeySpec} to process the byte array, which is the standard format returned by
     * {@code PublicKey.getEncoded()} and ensures **interoperability** and correct round-trip serialization.
     * The underlying key material retains the quantum-resistant properties of the ML-DSA algorithm.
     *
     * @param keyBytes The **X.509 encoded byte array** of the public key (e.g., the output of {@code PublicKey.getEncoded()}).
     * @return A valid ML-DSA {@link PublicKey} instance of type {@code BCDilithiumPublicKey}.
     * @throws CryptographicException if the key factory or key specification is invalid, or the byte array is malformed / corrupted (e.g., missing the X.509 wrapper).
     */
    public static PublicKey loadPublicKey(byte[] keyBytes) throws CryptographicException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_PAIR_ALGORITHM, BC_PROVIDER);
            KeySpec keySpec = new X509EncodedKeySpec(keyBytes);

            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new CryptographicException("Failed to load public key.", e);
        }
    }

    /**
     * **Core Method:** Signs a raw message byte array using the provided private key and the ML-DSA algorithm.
     * <p>
     * **Thread Safety:** A new {@link Signature} object is instantiated for every call to ensure the operation is thread-safe and isolated.
     *
     * @param privateKey The {@link PrivateKey} to sign the message with.
     * @param unsignedMessage The raw message bytes to sign.
     * @return The digital signature as a raw byte array.
     * @throws CryptographicException if the signing process fails due to an invalid key, unsupported algorithm, or a cryptographic error.
     */
    private static byte[] sign(PrivateKey privateKey, byte[] unsignedMessage) {
        try {
            Signature mlDsa = Signature.getInstance(KEY_PAIR_ALGORITHM, BC_PROVIDER);

            mlDsa.initSign(privateKey, new SecureRandom());
            mlDsa.update(unsignedMessage);

            return mlDsa.sign();
        } catch (Exception e) {
            log.error("Failed to sign data due to: {}", e.getMessage(), e);
            throw new CryptographicException("Failed to sign data.", e);
        }
    }

    /**
     * **Core Method:** Verifies a digital signature against the original message using the public key and the ML-DSA algorithm.
     * <p>
     * **Thread Safety:** A new {@link Signature} object is instantiated for every call.
     *
     * @param publicKey The {@link PublicKey} to verify the signature against.
     * @param originalMessage The original message bytes that were signed.
     * @param signature The digital signature bytes to verify.
     * @return {@code true} if the signature is valid for the message; {@code false} otherwise.
     * @throws CryptographicException if the verification setup fails (e.g., due to an invalid public key or crypto error).
     */
    private static boolean verifySignature(PublicKey publicKey, byte[] originalMessage, byte[] signature) {
        try {
            Signature mlDsa = Signature.getInstance(KEY_PAIR_ALGORITHM, BC_PROVIDER);

            mlDsa.initVerify(publicKey);
            mlDsa.update(originalMessage);

            return mlDsa.verify(signature);
        } catch (Exception e) {
            log.error("Failed to verify signature due to: {}", e.getMessage(), e);
            throw new CryptographicException("Failed to verify signature.", e);
        }
    }

    /**
     * Creates a self-signed **X.509 certificate** for a given key pair using the ML-DSA algorithm.
     * <p>
     * This certificate uses the provided distinguished name (e.g., the wallet ID) as both the **Issuer** and **Subject**.
     *
     * @param keyPair           The {@link KeyPair} to be certified (used for both the public key in the certificate and the private key for signing).
     * @param distinguishedName The unique identifier for the certificate owner (e.g., {@code "CN=COPO_abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789"}).
     * @return A self-signed {@link X509Certificate}.
     * @throws OperatorCreationException If the certificate signing process fails (e.g., invalid private key).
     * @throws CertificateException If the certificate building fails (e.g., invalid public key or malformed data).
     */
    public static X509Certificate selfSign(KeyPair keyPair, String distinguishedName) throws OperatorCreationException, CertificateException {
        X500Name issuer = new X500Name("CN=" + distinguishedName);
        Date startDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, 1);
        Date endDate = calendar.getTime();
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

        ContentSigner contentSigner = new JcaContentSignerBuilder(KEY_PAIR_ALGORITHM)
                .setProvider(BC_PROVIDER)
                .build(keyPair.getPrivate());

        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer, serial, startDate, endDate, issuer, keyPair.getPublic()
        );

        return new JcaX509CertificateConverter()
                .setProvider(BC_PROVIDER)
                .getCertificate(certBuilder.build(contentSigner));
    }
}
