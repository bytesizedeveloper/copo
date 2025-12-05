package org.acme.blockchain.wallet.service;

import org.acme.blockchain.common.exception.KeystoreException;
import org.acme.blockchain.test_common.factory.AddressTestFactory;
import org.acme.blockchain.wallet.utility.KeyPairUtility;
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

    private static KeyPair VALID_KEY_PAIR;

    private Path tempKeystorePath;

    @BeforeEach
    void setUp() {
        Mockito.when(passwordSupplier.get()).thenReturn("password123");

        tempKeystorePath = tempDir.resolve("test-keystore.p12");

        try {
            Field pathField = KeystoreService.class.getDeclaredField("path");
            pathField.setAccessible(true);
            pathField.set(keyStoreService, tempKeystorePath.toString());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set path field via reflection.", e);
        }

        VALID_KEY_PAIR = KeyPairUtility.generateKeyPair();
        Assertions.assertNotNull(VALID_KEY_PAIR, "Key pair must be generated.");
    }

    @Test
    void testWritePrivateKeyToKeystore_noFilePresent_createsFileAndWritesKey() {
        // Given
        String alias = AddressTestFactory.getAddressString();

        Assertions.assertFalse(Files.exists(tempKeystorePath), "Keystore file should not exist before test.");

        // When
        keyStoreService.writePrivateKeyToKeystore(VALID_KEY_PAIR, alias);

        // Then
        Assertions.assertTrue(Files.exists(tempKeystorePath), "Keystore file should be created.");

        PrivateKey retrievedKey = keyStoreService.readPrivateKeyFromKeystore(alias);
        Assertions.assertEquals(VALID_KEY_PAIR.getPrivate(), retrievedKey);
    }

    @Test
    void testWritePrivateKeyToKeystore_filePresent_writesAdditionalKey() {
        // Given
        String alias = AddressTestFactory.getAddressString();

        KeyPair additionalKeypair = KeyPairUtility.generateKeyPair();
        String additionalAlias = AddressTestFactory.getAddressString();

        // When
        keyStoreService.writePrivateKeyToKeystore(VALID_KEY_PAIR, alias);

        Assertions.assertTrue(Files.exists(tempKeystorePath), "Keystore file should be created.");

        keyStoreService.writePrivateKeyToKeystore(additionalKeypair, additionalAlias);

        PrivateKey retrievedKeyAlpha = keyStoreService.readPrivateKeyFromKeystore(alias);
        Assertions.assertEquals(VALID_KEY_PAIR.getPrivate(), retrievedKeyAlpha);

        PrivateKey retrievedKeyBeta = keyStoreService.readPrivateKeyFromKeystore(additionalAlias);
        Assertions.assertEquals(additionalKeypair.getPrivate(), retrievedKeyBeta);
    }

    @Test
    void readPrivateKeyFromKeystore_keystoreNotFound_throwsKeystoreException() throws IOException {
        // Given
        String alias = AddressTestFactory.getAddressString();

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
        String alias = AddressTestFactory.getAddressString();

        keyStoreService.writePrivateKeyToKeystore(VALID_KEY_PAIR, alias);

        // Then
        Exception thrown = Assertions.assertThrows(Exception.class, () -> keyStoreService.readPrivateKeyFromKeystore("wrong alias"), "Exception should be thrown in the event of alias not found.");

        Assertions.assertInstanceOf(KeystoreException.class, thrown, "Exception should indicate alias not found.");
        Assertions.assertEquals("Failed to read data from keystore (" + tempKeystorePath + ") for alias 'wrong alias' " +
                "due to keystore entry not not found for alias.", thrown.getMessage());
    }

    @Test
    void testReadPrivateKeyFromKeystore_incorrectPassword_throwsKeystoreException() {
        // Given
        String alias = AddressTestFactory.getAddressString();

        keyStoreService.writePrivateKeyToKeystore(VALID_KEY_PAIR, alias);

        // When
        Mockito.when(passwordSupplier.get()).thenReturn("wrong password");

        // Then
        Exception thrown = Assertions.assertThrows(Exception.class, () -> keyStoreService.readPrivateKeyFromKeystore(alias), "Exception should be thrown in the event of incorrect password.");

        Assertions.assertInstanceOf(KeystoreException.class, thrown, "Exception should indicate an incorrect password.");
        Assertions.assertEquals("Failed to read data from keystore (" + tempKeystorePath + ") for alias '" + alias +
                "' due to keystore password was incorrect", thrown.getMessage());
    }
}
