package org.acme.blockchain.wallet.utility;

import org.acme.blockchain.common.exception.CryptographicException;
import org.acme.blockchain.common.utility.HashUtility;
import org.acme.blockchain.test_common.test_data.WalletTestData;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;

public class KeyPairUtilityTest {

    private static KeyPair VALID_KEY_PAIR;

    @BeforeAll
    static void setup() {
        VALID_KEY_PAIR = KeyPairUtility.generateKeyPair();
        Assertions.assertNotNull(VALID_KEY_PAIR, "Key pair must be generated.");
    }

    @Test
    void generateKeyPair_returnsValidMLDSAKeys() {
        // Given & When - keypair generated in setup

        // Then
        Assertions.assertNotNull(VALID_KEY_PAIR.getPublic(), "Public key should not be null.");
        Assertions.assertNotNull(VALID_KEY_PAIR.getPrivate(), "Private key should not be null.");

        Assertions.assertEquals(MLDSAParameterSpec.ml_dsa_87.getName(), VALID_KEY_PAIR.getPublic().getAlgorithm(), "Public key algorithm must be ML-DSA.");
        Assertions.assertEquals(MLDSAParameterSpec.ml_dsa_87.getName(), VALID_KEY_PAIR.getPrivate().getAlgorithm(), "Private key algorithm must be ML-DSA.");
    }

    @Test
    void testLoadPublicKey_deserializeKey() {
        // Given
        byte[] publicKeyEncoded = VALID_KEY_PAIR.getPublic().getEncoded();

        // When
        PublicKey loadedKey = KeyPairUtility.loadPublicKey(publicKeyEncoded);

        // Then
        Assertions.assertNotNull(loadedKey, "Loaded public key should not be null.");
        Assertions.assertEquals(VALID_KEY_PAIR.getPublic(), loadedKey, "Loaded public key must match original public key.");
    }

    @Test
    void testLoadPublicKey_malformedBytes_throwsCryptographicException() {
        // Given
        byte[] malformedKey = Arrays.copyOf(VALID_KEY_PAIR.getPublic().getEncoded(), 10);

        // When & Then
        Assertions.assertThrows(CryptographicException.class, () -> KeyPairUtility.loadPublicKey(malformedKey), "Exception should be thrown in the event of malformed bytes.");
    }

    @Test
    void testSignAndVerifySignature_validKeyAndSignature() {
        // Given
        String unsignedMessage = "data";

        // When
        byte[] signatureBytes = KeyPairUtility.sign(VALID_KEY_PAIR.getPrivate(), unsignedMessage);
        String signatureHex = HashUtility.bytesToHex(signatureBytes);

        // Then
        Assertions.assertTrue(KeyPairUtility.verifySignature(VALID_KEY_PAIR.getPublic(), unsignedMessage, signatureHex), "Verification must succeed with the correct key and message.");
    }

    @Test
    void testSignAndVerifySignature_modifiedMessage() {
        // Given
        String unsignedMessage = "data";

        // When
        byte[] signatureBytes = KeyPairUtility.sign(VALID_KEY_PAIR.getPrivate(), unsignedMessage);
        String signatureHex = HashUtility.bytesToHex(signatureBytes);

        // Then
        Assertions.assertFalse(KeyPairUtility.verifySignature(VALID_KEY_PAIR.getPublic(), "tampered" + unsignedMessage, signatureHex), "Verification must fail when the message is modified.");
    }

    @Test
    void testSignAndVerifySignature_wrongPublicKey() {
        // Given
        KeyPair wrongKeyPair = KeyPairUtility.generateKeyPair();

        String unsignedMessage = "data";

        // When
        byte[] signatureBytes = KeyPairUtility.sign(VALID_KEY_PAIR.getPrivate(), unsignedMessage);
        String signatureHex = HashUtility.bytesToHex(signatureBytes);

        // Then
        Assertions.assertFalse(KeyPairUtility.verifySignature(wrongKeyPair.getPublic(), unsignedMessage, signatureHex), "Verification must fail when using a different public key.");
    }

    @Test
    void testVerifySignature_malformedHexString_throwsIllegalArgumentException() {
        // Given
        String badHexSignature = "ABC123XYZ";

        String unsignedMessage = "data";

        // When & Then
        Assertions.assertThrows(IllegalArgumentException.class, () -> KeyPairUtility.verifySignature(VALID_KEY_PAIR.getPublic(), unsignedMessage, badHexSignature), "Exception should be thrown in the event of malformed hex signature.");
    }

    @Test
    void selfSign_generatesValidCertificate() throws Exception {
        // When
        X509Certificate certificate = KeyPairUtility.selfSign(VALID_KEY_PAIR, WalletTestData.ADDRESS_ALPHA.value());

        // Then
        Assertions.assertNotNull(certificate, "Certificate should be generated.");

        Assertions.assertTrue(certificate.getSubjectX500Principal().getName().contains(WalletTestData.ADDRESS_ALPHA.value()), "Subject must contain the alias.");
        Assertions.assertEquals(certificate.getIssuerX500Principal(), certificate.getSubjectX500Principal(), "Certificate must be self-signed (Issuer == Subject).");

        Assertions.assertDoesNotThrow(() -> certificate.checkValidity(new Date()), "Certificate should be valid at the current time.");

        Assertions.assertDoesNotThrow(() -> certificate.verify(VALID_KEY_PAIR.getPublic()), "Certificate must be verifiable by its own public key.");

        Assertions.assertNotEquals(BigInteger.ZERO, certificate.getSerialNumber());
    }
}
