package org.acme.blockchain.wallet.service;

import org.acme.blockchain.common.exception.CryptographicException;
import org.acme.blockchain.common.service.KeyStoreService;
import org.acme.blockchain.common.utility.HashUtility;
import org.acme.blockchain.common.utility.KeyPairUtility;
import org.acme.blockchain.wallet.model.WalletModel;
import org.acme.blockchain.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.util.HexFormat;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    WalletRepository walletRepository;

    @Mock
    KeyStoreService keyStoreService;

    @InjectMocks
    WalletService walletService;

    @Test
    void testCreate_returnsWalletModelAndPersistsKey() throws Exception {
        // Given
        KeyPair keyPair = KeyPairGenerator.getInstance("ML-DSA-87", "BC").generateKeyPair();

        try (MockedStatic<KeyPairUtility> keyPairUtilityMock = Mockito.mockStatic(KeyPairUtility.class);
             MockedStatic<HashUtility> hashUtilityMock = Mockito.mockStatic(HashUtility.class)) {

            byte[] publicKeyEncoded = keyPair.getPublic().getEncoded();
            byte[] sha256 = MessageDigest.getInstance("SHA-256").digest(publicKeyEncoded);
            byte[] blake2b = MessageDigest.getInstance("BLAKE2b-256").digest(sha256);
            String hex = HexFormat.of().formatHex(blake2b).toLowerCase();

            String address = "COPO_" + hex;

            // When
            keyPairUtilityMock.when(KeyPairUtility::generateKeyPair).thenReturn(keyPair);

            hashUtilityMock.when(() -> HashUtility.calculateSHA256(publicKeyEncoded)).thenReturn(sha256);
            hashUtilityMock.when(() -> HashUtility.calculateBLAKE2b256(sha256)).thenReturn(blake2b);
            hashUtilityMock.when(() -> HashUtility.bytesToHex(blake2b)).thenReturn(hex);

            Mockito.when(walletRepository.exists(address)).thenReturn(false);

            // Then
            WalletModel result = walletService.create();

            Mockito.verify(keyStoreService, Mockito.times(1)).writePrivateKeyToKeyStore(
                    Mockito.eq(keyPair),
                    Mockito.eq(address)
            );

            Mockito.verify(walletRepository, Mockito.times(1)).insert(Mockito.any(WalletModel.class));

            Assertions.assertEquals(address, result.getAddress(), "Wallet address should match the derived hash.");
            Assertions.assertEquals(keyPair, result.getKeyPair(), "WalletModel should contain the generated KeyPair.");
        }
    }

    @Test
    void testCreate_collisionDetected() throws Exception {
        // Given
        KeyPair keyPair = KeyPairGenerator.getInstance("ML-DSA-87", "BC").generateKeyPair();

        try (MockedStatic<KeyPairUtility> keyPairUtilityMock = Mockito.mockStatic(KeyPairUtility.class);
             MockedStatic<HashUtility> hashUtilityMock = Mockito.mockStatic(HashUtility.class)) {

            byte[] publicKeyEncoded = keyPair.getPublic().getEncoded();
            byte[] sha256 = MessageDigest.getInstance("SHA-256").digest(publicKeyEncoded);
            byte[] blake2b = MessageDigest.getInstance("BLAKE2b-256").digest(sha256);
            String hex = HexFormat.of().formatHex(blake2b).toLowerCase();

            String address = "COPO_" + hex;

            // When
            keyPairUtilityMock.when(KeyPairUtility::generateKeyPair).thenReturn(keyPair);

            hashUtilityMock.when(() -> HashUtility.calculateSHA256(publicKeyEncoded)).thenReturn(sha256);
            hashUtilityMock.when(() -> HashUtility.calculateBLAKE2b256(sha256)).thenReturn(blake2b);
            hashUtilityMock.when(() -> HashUtility.bytesToHex(blake2b)).thenReturn(hex);

            Mockito.when(walletRepository.exists(address)).thenReturn(true);

            // Then
            Exception thrown = Assertions.assertThrows(Exception.class, () -> walletService.create(), "The exception thrown due to the collision must be re-thrown by WalletService.");

            Assertions.assertInstanceOf(IllegalStateException.class, thrown, "The re-thrown exception should be the original exception.");

            Mockito.verify(keyStoreService, Mockito.never()).writePrivateKeyToKeyStore(Mockito.any(), Mockito.any());
            Mockito.verify(walletRepository, Mockito.never()).insert(Mockito.any());
        }
    }

    @Test
    void testCreate_keyPairGenerationFails_throwsExceptionAndClearsPassword() {
        // Given
        Exception exception = new CryptographicException("KeyPair generation failure.");

        try (MockedStatic<KeyPairUtility> keyPairUtilityMock = Mockito.mockStatic(KeyPairUtility.class)) {
            // When
            keyPairUtilityMock.when(KeyPairUtility::generateKeyPair).thenThrow(exception);

            // Then
            Exception thrown = Assertions.assertThrows(Exception.class, () -> walletService.create(), "The exception thrown by KeyPairUtility must be re-thrown by WalletService.");

            Assertions.assertEquals(exception, thrown, "The re-thrown exception should be the original exception.");

            Mockito.verify(walletRepository, Mockito.never()).exists(Mockito.any());
            Mockito.verify(keyStoreService, Mockito.never()).writePrivateKeyToKeyStore(Mockito.any(), Mockito.any());
            Mockito.verify(walletRepository, Mockito.never()).insert(Mockito.any());
        }
    }

    @Test
    void testCreate_persistenceFails_throwsExceptionAndClearsPassword() {
        // Given
        Exception exception = new CryptographicException("Persistence failure.");

        // When
        Mockito.doThrow(exception).when(keyStoreService).writePrivateKeyToKeyStore(
                Mockito.any(KeyPair.class), Mockito.any(String.class)
        );

        // Then
        Exception thrown = Assertions.assertThrows(Exception.class, () -> walletService.create(), "The exception thrown by KeyStoreService must be re-thrown by WalletService.");

        Assertions.assertEquals(exception, thrown, "The re-thrown exception should be the original exception.");

        Mockito.verify(walletRepository, Mockito.never()).insert(Mockito.any());
    }
}
