package org.acme.common.utility;

import org.acme.common.exception.CryptographicException;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

/**
 * A specialized utility class for generating, signing, and verifying cryptographic
 * operations related to the ML-DSA (Dilithium) quantum-resistant algorithm.
 * <p>
 * This class ensures thread-safe access to core cryptographic functions by creating
 * a new {@link Signature} instance for each operation, leveraging the Bouncy Castle
 * provider for Dilithium-specific algorithms.
 */
public final class KeyPairUtility {

    private static final String KEY_PAIR_ALGORITHM = "ML-DSA-87";

    private static final String BC_PROVIDER = "BC";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Generates a new ML-DSA key pair with the Dilithium 5 security level.
     *
     * @return A cryptographically secure {@link KeyPair} consisting of a public and private key.
     * @throws CryptographicException if the algorithm or provider is not available in the environment.
     */
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_PAIR_ALGORITHM, BC_PROVIDER);
            return keyGen.generateKeyPair();
        } catch (Exception e) {
            throw new CryptographicException("Failed to generate key pair.", e);
        }
    }

    /**
     * Signs a message using the provided private key and the ML-DSA algorithm.
     *
     * @param privateKey      The private key to sign with.
     * @param unsignedMessage The message bytes to sign.
     * @return The digital signature as a byte array.
     * @throws CryptographicException if the signing process fails due to an invalid key or a crypto error.
     */
    public static byte[] sign(PrivateKey privateKey, String unsignedMessage) {
        return sign(privateKey, unsignedMessage.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Signs a message using the provided private key and the ML-DSA algorithm.
     *
     * @param privateKey      The private key to sign with.
     * @param unsignedMessage The message bytes to sign.
     * @return The digital signature as a byte array.
     * @throws CryptographicException if the signing process fails due to an invalid key or a crypto error.
     */
    public static byte[] sign(PrivateKey privateKey, byte[] unsignedMessage) {
        try {
            Signature signature = Signature.getInstance(KEY_PAIR_ALGORITHM, BC_PROVIDER);
            signature.initSign(privateKey);
            signature.update(unsignedMessage);

            return signature.sign();
        } catch (Exception e) {
            throw new CryptographicException("Failed to sign data.", e);
        }
    }

    /**
     * Verifies a digital signature against the original message using the public key and the ML-DSA algorithm.
     *
     * @param publicKey       The public key to verify with.
     * @param originalMessage The original message bytes that were signed.
     * @param signatureBytes  The digital signature bytes to verify.
     * @return {@code true} if the signature is valid for the message; {@code false} otherwise.
     * @throws CryptographicException if the verification setup fails (e.g., due to an invalid key or crypto error).
     */
    public static boolean verifySignature(PublicKey publicKey, byte[] originalMessage, byte[] signatureBytes) {
        try {
            Signature signature = Signature.getInstance(KEY_PAIR_ALGORITHM, BC_PROVIDER);
            signature.initVerify(publicKey);
            signature.update(originalMessage);

            return signature.verify(signatureBytes);
        } catch (Exception e) {
            throw new CryptographicException("Failed to verify signature.", e);
        }
    }

    /**
     * Creates a self-signed X.509 certificate for a given key pair using the ML-DSA algorithm.
     * This certificate is commonly used for KeyStore entry identification.
     *
     * @param keyPair           The key pair to be certified.
     * @param distinguishedName The unique identifier for the certificate owner (e.g., CN=Wallet-ID).
     * @return A self-signed {@link X509Certificate}.
     * @throws OperatorCreationException If the certificate signing process fails.
     * @throws CertificateException If the certificate building fails.
     */
    public static X509Certificate selfSign(KeyPair keyPair, String distinguishedName) throws OperatorCreationException, CertificateException {
        X500Name issuer = new X500Name("CN=" + distinguishedName);
        Date startDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, 1); // Valid for one year
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
