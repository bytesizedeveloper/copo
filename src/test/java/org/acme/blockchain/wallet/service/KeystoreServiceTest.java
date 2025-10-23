package org.acme.blockchain.wallet.service;

import org.acme.blockchain.common.exception.KeystoreException;
import org.acme.blockchain.test_common.test_data.WalletTestData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.function.Supplier;

@ExtendWith(MockitoExtension.class)
public class KeystoreServiceTest {

    @Mock
    private Supplier<String> passwordSupplier;

    @InjectMocks
    private KeystoreService keyStoreService;

    @TempDir
    Path tempDir;

    private Path tempKeystorePath;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        Mockito.when(passwordSupplier.get()).thenReturn("password123");

        tempKeystorePath = tempDir.resolve("test-keystore.p12");

        try {
            Field pathField = KeystoreService.class.getDeclaredField("path");
            pathField.setAccessible(true);
            pathField.set(keyStoreService, tempKeystorePath.toString());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set path field via reflection.", e);
        }
    }

    @Test
    void testWritePrivateKeyToKeystore_noFilePresent_createsFileAndWritesKey() {
        // Given
        KeyPair keyPair = WalletTestData.KEYPAIR_ALPHA;
        String alias = WalletTestData.ADDRESS_ALPHA.value();

        Assertions.assertFalse(Files.exists(tempKeystorePath), "Keystore file should not exist before test.");

        // When
        keyStoreService.writePrivateKeyToKeystore(keyPair, alias);

        // Then
        Assertions.assertTrue(Files.exists(tempKeystorePath), "Keystore file should be created.");

        PrivateKey retrievedKey = keyStoreService.readPrivateKeyFromKeystore(alias);
        Assertions.assertEquals(keyPair.getPrivate(), retrievedKey);
    }

    @Test
    void testWritePrivateKeyToKeystore_filePresent_writesSecondKey() {
        // Given
        KeyPair keyPairAlpha = WalletTestData.KEYPAIR_ALPHA;
        String aliasAlpha = WalletTestData.ADDRESS_ALPHA.value();

        KeyPair keyPairBeta = WalletTestData.KEYPAIR_BETA;
        String aliasBeta = WalletTestData.ADDRESS_BETA.value();

        // When
        keyStoreService.writePrivateKeyToKeystore(keyPairAlpha, aliasAlpha);

        Assertions.assertTrue(Files.exists(tempKeystorePath), "Keystore file should be created.");

        keyStoreService.writePrivateKeyToKeystore(keyPairBeta, aliasBeta);

        PrivateKey retrievedKeyAlpha = keyStoreService.readPrivateKeyFromKeystore(aliasAlpha);
        Assertions.assertEquals(keyPairAlpha.getPrivate(), retrievedKeyAlpha);

        PrivateKey retrievedKeyBeta = keyStoreService.readPrivateKeyFromKeystore(aliasBeta);
        Assertions.assertEquals(keyPairBeta.getPrivate(), retrievedKeyBeta);
    }

    @Test
    void readPrivateKeyFromKeystore_keystoreNotFound_throwsKeystoreException() throws IOException {
        // Given
        String alias = WalletTestData.ADDRESS_ALPHA.value();

        Files.deleteIfExists(tempKeystorePath);

        // Then
        Exception thrown = Assertions.assertThrows(Exception.class, () -> keyStoreService.readPrivateKeyFromKeystore(alias), "Exception should be thrown in the event of no keystore found.");

        Assertions.assertInstanceOf(KeystoreException.class, thrown, "Exception should indicate no keystore found.");
        Assertions.assertEquals("Failed to read data from keystore (" + tempKeystorePath + ") for alias '" + alias +
                "' due to keystore file not being found.", thrown.getMessage());
    }

    @Test
    void testReadPrivateKeyFromKeystore_aliasNotFound_throwsKeystoreException() {
        // Given
        KeyPair keyPair = WalletTestData.KEYPAIR_ALPHA;
        String alias = WalletTestData.ADDRESS_ALPHA.value();

        keyStoreService.writePrivateKeyToKeystore(keyPair, alias);

        // Then
        Exception thrown = Assertions.assertThrows(Exception.class, () -> keyStoreService.readPrivateKeyFromKeystore("wrong alias"), "Exception should be thrown in the event of alias not found.");

        Assertions.assertInstanceOf(KeystoreException.class, thrown, "Exception should indicate alias not found.");
        Assertions.assertEquals("Failed to read data from keystore (" + tempKeystorePath + ") for alias 'wrong alias' " +
                "due to keystore entry not not found for alias.", thrown.getMessage());
    }

    @Test
    void testReadPrivateKeyFromKeystore_incorrectPassword_throwsKeystoreException() {
        // Given
        KeyPair keyPair = WalletTestData.KEYPAIR_ALPHA;
        String alias = WalletTestData.ADDRESS_ALPHA.value();

        keyStoreService.writePrivateKeyToKeystore(keyPair, alias);

        // When
        Mockito.when(passwordSupplier.get()).thenReturn("wrong password");

        // Then
        Exception thrown = Assertions.assertThrows(Exception.class, () -> keyStoreService.readPrivateKeyFromKeystore(alias), "Exception should be thrown in the event of incorrect password.");

        Assertions.assertInstanceOf(KeystoreException.class, thrown, "Exception should indicate an incorrect password.");
        Assertions.assertEquals("Failed to read data from keystore (" + tempKeystorePath + ") for alias '" + alias +
                "' due to keystore password was incorrect", thrown.getMessage());
    }
}
