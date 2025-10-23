package org.acme.blockchain.wallet.service;

import jakarta.ws.rs.NotFoundException;
import org.acme.blockchain.common.exception.CryptographicException;
import org.acme.blockchain.common.exception.KeystoreException;
import org.acme.blockchain.common.model.AddressModel;
import org.acme.blockchain.common.utility.HashUtility;
import org.acme.blockchain.wallet.utility.KeyPairUtility;
import org.acme.blockchain.test_common.test_data.WalletTestData;
import org.acme.blockchain.wallet.model.WalletModel;
import org.acme.blockchain.wallet.repository.WalletRepository;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.NoDataFoundException;
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
    KeystoreService keyStoreService;

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

            WalletModel result = walletService.create();

            // Then
            Mockito.verify(walletRepository, Mockito.times(1)).exists(address);
            Mockito.verify(keyStoreService, Mockito.times(1)).writePrivateKeyToKeystore(keyPair, address);
            Mockito.verify(walletRepository, Mockito.times(1)).insert(Mockito.any(WalletModel.class));

            Assertions.assertEquals(address, result.address().value(), "Wallet address should match the calculated hash.");
            Assertions.assertEquals(keyPair, result.keyPair(), "WalletModel should contain the generated KeyPair.");
            Assertions.assertNotNull(result.publicKeyEncoded(), "WalletModel should contain a valid encoded public key.");
            Assertions.assertNotNull(result.createdAt(), "WalletModel should contain a valid timestamp.");
        }
    }

    @Test
    void testCreate_keyPairGenerationFailure_throwsCryptographicException() {
        // When
        try (MockedStatic<KeyPairUtility> keyPairUtilityMock = Mockito.mockStatic(KeyPairUtility.class)) {
            keyPairUtilityMock.when(KeyPairUtility::generateKeyPair).thenThrow(CryptographicException.class);

            // Then
            Assertions.assertThrows(CryptographicException.class, () -> walletService.create(), "Exception should be thrown in the event of a keypair generation failure.");

            Mockito.verify(walletRepository, Mockito.never()).exists(Mockito.any());
            Mockito.verify(keyStoreService, Mockito.never()).writePrivateKeyToKeystore(Mockito.any(), Mockito.any());
            Mockito.verify(walletRepository, Mockito.never()).insert(Mockito.any());
        }
    }

    @Test
    void testCreate_addressGenerationFailure_sha256Hash_throwsIllegalArgumentException() throws Exception {
        // Given
        KeyPair keyPair = KeyPairGenerator.getInstance("ML-DSA-87", "BC").generateKeyPair();

        try (MockedStatic<KeyPairUtility> keyPairUtilityMock = Mockito.mockStatic(KeyPairUtility.class);
             MockedStatic<HashUtility> hashUtilityMock = Mockito.mockStatic(HashUtility.class)) {

            // When
            keyPairUtilityMock.when(KeyPairUtility::generateKeyPair).thenReturn(keyPair);

            // Then
            Assertions.assertThrows(IllegalArgumentException.class, () -> walletService.create(), "Exception should be thrown in the event of a address generation failure.");

            Mockito.verify(walletRepository, Mockito.never()).exists(Mockito.any());
            Mockito.verify(keyStoreService, Mockito.never()).writePrivateKeyToKeystore(Mockito.any(), Mockito.any());
            Mockito.verify(walletRepository, Mockito.never()).insert(Mockito.any());
        }
    }

    @Test
    void testCreate_addressGenerationFailure_sha256Hash_throwsCryptographicException() throws Exception {
        // Given
        KeyPair keyPair = KeyPairGenerator.getInstance("ML-DSA-87", "BC").generateKeyPair();

        try (MockedStatic<KeyPairUtility> keyPairUtilityMock = Mockito.mockStatic(KeyPairUtility.class);
             MockedStatic<HashUtility> hashUtilityMock = Mockito.mockStatic(HashUtility.class)) {

            byte[] publicKeyEncoded = keyPair.getPublic().getEncoded();

            // When
            keyPairUtilityMock.when(KeyPairUtility::generateKeyPair).thenReturn(keyPair);

            hashUtilityMock.when(() -> HashUtility.calculateSHA256(publicKeyEncoded)).thenThrow(CryptographicException.class);

            // Then
            Assertions.assertThrows(CryptographicException.class, () -> walletService.create(), "Exception should be thrown in the event of a address generation failure.");

            Mockito.verify(walletRepository, Mockito.never()).exists(Mockito.any());
            Mockito.verify(keyStoreService, Mockito.never()).writePrivateKeyToKeystore(Mockito.any(), Mockito.any());
            Mockito.verify(walletRepository, Mockito.never()).insert(Mockito.any());
        }
    }

    @Test
    void testCreate_addressGenerationFailure_blake2bHash_throwsIllegalArgumentException() throws Exception {
        // Given
        KeyPair keyPair = KeyPairGenerator.getInstance("ML-DSA-87", "BC").generateKeyPair();

        try (MockedStatic<KeyPairUtility> keyPairUtilityMock = Mockito.mockStatic(KeyPairUtility.class);
             MockedStatic<HashUtility> hashUtilityMock = Mockito.mockStatic(HashUtility.class)) {

            byte[] publicKeyEncoded = keyPair.getPublic().getEncoded();

            // When
            keyPairUtilityMock.when(KeyPairUtility::generateKeyPair).thenReturn(keyPair);

            hashUtilityMock.when(() -> HashUtility.calculateSHA256(publicKeyEncoded)).thenReturn(null);

            // Then
            Assertions.assertThrows(IllegalArgumentException.class, () -> walletService.create(), "Exception should be thrown in the event of a address generation failure.");

            Mockito.verify(walletRepository, Mockito.never()).exists(Mockito.any());
            Mockito.verify(keyStoreService, Mockito.never()).writePrivateKeyToKeystore(Mockito.any(), Mockito.any());
            Mockito.verify(walletRepository, Mockito.never()).insert(Mockito.any());
        }
    }

    @Test
    void testCreate_addressGenerationFailure_blake2bHash_throwsCryptographicException() throws Exception {
        // Given
        KeyPair keyPair = KeyPairGenerator.getInstance("ML-DSA-87", "BC").generateKeyPair();

        try (MockedStatic<KeyPairUtility> keyPairUtilityMock = Mockito.mockStatic(KeyPairUtility.class);
             MockedStatic<HashUtility> hashUtilityMock = Mockito.mockStatic(HashUtility.class)) {

            byte[] publicKeyEncoded = keyPair.getPublic().getEncoded();
            byte[] sha256 = MessageDigest.getInstance("SHA-256").digest(publicKeyEncoded);

            // When
            keyPairUtilityMock.when(KeyPairUtility::generateKeyPair).thenReturn(keyPair);

            hashUtilityMock.when(() -> HashUtility.calculateSHA256(publicKeyEncoded)).thenReturn(sha256);
            hashUtilityMock.when(() -> HashUtility.calculateBLAKE2b256(sha256)).thenThrow(CryptographicException.class);

            // Then
            Assertions.assertThrows(CryptographicException.class, () -> walletService.create(), "Exception should be thrown in the event of a address generation failure.");

            Mockito.verify(walletRepository, Mockito.never()).exists(Mockito.any());
            Mockito.verify(keyStoreService, Mockito.never()).writePrivateKeyToKeystore(Mockito.any(), Mockito.any());
            Mockito.verify(walletRepository, Mockito.never()).insert(Mockito.any());
        }
    }

    @Test
    void testCreate_addressGenerationFailure_toHex_throwsIllegalArgumentException() throws Exception {
        // Given
        KeyPair keyPair = KeyPairGenerator.getInstance("ML-DSA-87", "BC").generateKeyPair();

        try (MockedStatic<KeyPairUtility> keyPairUtilityMock = Mockito.mockStatic(KeyPairUtility.class);
             MockedStatic<HashUtility> hashUtilityMock = Mockito.mockStatic(HashUtility.class)) {

            byte[] publicKeyEncoded = keyPair.getPublic().getEncoded();
            byte[] sha256 = MessageDigest.getInstance("SHA-256").digest(publicKeyEncoded);

            // When
            keyPairUtilityMock.when(KeyPairUtility::generateKeyPair).thenReturn(keyPair);

            hashUtilityMock.when(() -> HashUtility.calculateSHA256(publicKeyEncoded)).thenReturn(null);
            hashUtilityMock.when(() -> HashUtility.calculateBLAKE2b256(sha256)).thenReturn(null);

            // Then
            Assertions.assertThrows(IllegalArgumentException.class, () -> walletService.create(), "Exception should be thrown in the event of a address generation failure.");

            Mockito.verify(walletRepository, Mockito.never()).exists(Mockito.any());
            Mockito.verify(keyStoreService, Mockito.never()).writePrivateKeyToKeystore(Mockito.any(), Mockito.any());
            Mockito.verify(walletRepository, Mockito.never()).insert(Mockito.any());
        }
    }

    @Test
    void testCreate_collisionDetected_throwsIllegalStateException() throws Exception {
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
            Exception thrown = Assertions.assertThrows(IllegalStateException.class, () -> walletService.create(), "Exception should be thrown in the event of an address collision.");
            Assertions.assertEquals("A wallet with this address already exists.", thrown.getMessage());

            Mockito.verify(walletRepository, Mockito.times(1)).exists(address);
            Mockito.verify(keyStoreService, Mockito.never()).writePrivateKeyToKeystore(Mockito.any(), Mockito.any());
            Mockito.verify(walletRepository, Mockito.never()).insert(Mockito.any());
        }
    }

    @Test
    void testCreate_keystoreFailure_throwsKeystoreException() throws Exception {
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
            Mockito.doThrow(KeystoreException.class).when(keyStoreService).writePrivateKeyToKeystore(keyPair, address);

            // Then
            Assertions.assertThrows(KeystoreException.class, () -> walletService.create(), "Exception should be thrown in the event of keystore failure.");

            Mockito.verify(walletRepository, Mockito.times(1)).exists(address);
            Mockito.verify(keyStoreService, Mockito.times(1)).writePrivateKeyToKeystore(keyPair, address);
            Mockito.verify(walletRepository, Mockito.never()).insert(Mockito.any(WalletModel.class));
        }
    }

    @Test
    void testCreate_keystoreFailure_throwsCryptographicException() throws Exception {
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
            Mockito.doThrow(CryptographicException.class).when(keyStoreService).writePrivateKeyToKeystore(keyPair, address);

            // Then
            Assertions.assertThrows(CryptographicException.class, () -> walletService.create(), "Exception should be thrown in the event of keystore failure.");

            Mockito.verify(walletRepository, Mockito.times(1)).exists(address);
            Mockito.verify(keyStoreService, Mockito.times(1)).writePrivateKeyToKeystore(keyPair, address);
            Mockito.verify(walletRepository, Mockito.never()).insert(Mockito.any(WalletModel.class));
        }
    }

    @Test
    void testCreate_publicKeyPersistenceFailure_throwsDataAccessException() throws Exception {
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
            Mockito.doThrow(DataAccessException.class).when(walletRepository).insert(Mockito.any(WalletModel.class));

            // Then
            Assertions.assertThrows(DataAccessException.class, () -> walletService.create(), "Exception should be thrown in the event of public key persistence failure.");

            Mockito.verify(walletRepository, Mockito.times(1)).exists(address);
            Mockito.verify(keyStoreService, Mockito.times(1)).writePrivateKeyToKeystore(keyPair, address);
            Mockito.verify(walletRepository, Mockito.times(1)).insert(Mockito.any(WalletModel.class));
        }
    }

    @Test
    void testGet_retrievesWallet() {
        // Given
        AddressModel address = WalletTestData.ADDRESS_ALPHA;

        WalletModel wallet = WalletTestData.getWalletAlpha();

        // When
        Mockito.when(walletRepository.retrieveWalletByAddress(address.value())).thenReturn(wallet);

        WalletModel returned = walletService.get(address);

        // Then
        Assertions.assertEquals(wallet, returned);

        Mockito.verify(walletRepository, Mockito.times(1)).retrieveWalletByAddress(address.value());
    }

    @Test
    void testGet_addressNotFound_throwsNotFoundException() {
        // Given
        AddressModel address = WalletTestData.ADDRESS_ALPHA;

        // When
        Mockito.when(walletRepository.retrieveWalletByAddress(address.value())).thenThrow(NoDataFoundException.class);

        // Then
        Exception thrown = Assertions.assertThrows(NotFoundException.class, () -> walletService.get(address), "Exception should be thrown in the event of no wallet found using address.");
        Assertions.assertEquals("Wallet does not exist in the database: " + address.value(), thrown.getMessage());

        Mockito.verify(walletRepository, Mockito.times(1)).retrieveWalletByAddress(address.value());
    }

    @Test
    void testGet_queryFailure_throwsDataAccessException() {
        // Given
        AddressModel address = WalletTestData.ADDRESS_ALPHA;

        // When
        Mockito.when(walletRepository.retrieveWalletByAddress(address.value())).thenThrow(DataAccessException.class);

        // Then
        Assertions.assertThrows(DataAccessException.class, () -> walletService.get(address), "Exception should be thrown in the event of database query failure.");

        Mockito.verify(walletRepository, Mockito.times(1)).retrieveWalletByAddress(address.value());
    }

    @Test
    void testSign_returnsSignature() {
        // Given
        AddressModel address = WalletTestData.ADDRESS_ALPHA;

        KeyPair keyPair = WalletTestData.KEYPAIR_ALPHA;

        // When
        try (MockedStatic<KeyPairUtility> keyPairUtilityMock = Mockito.mockStatic(KeyPairUtility.class)) {

            Mockito.when(keyStoreService.readPrivateKeyFromKeystore(address.value())).thenReturn(keyPair.getPrivate());

            keyPairUtilityMock.when(() -> KeyPairUtility.sign(Mockito.any(), Mockito.any())).thenReturn(new byte[]{});

            String signature = walletService.sign(address, "message");

            // Then
            Assertions.assertNotNull(signature);

            Mockito.verify(keyStoreService, Mockito.times(1)).readPrivateKeyFromKeystore(address.value());
        }
    }

    @Test
    void testSign_keystoreFailure_throwsKeystoreException() {
        // Given
        AddressModel address = WalletTestData.ADDRESS_ALPHA;

        // When
        try (MockedStatic<KeyPairUtility> keyPairUtilityMock = Mockito.mockStatic(KeyPairUtility.class)) {

            Mockito.when(keyStoreService.readPrivateKeyFromKeystore(address.value())).thenThrow(KeystoreException.class);

            // Then
            Exception thrown = Assertions.assertThrows(Exception.class, () -> walletService.sign(address, "message"), "Exception should be thrown in the event of keystore failure.");

            Assertions.assertInstanceOf(KeystoreException.class, thrown, "Exception should indicate keystore failure.");

            Mockito.verify(keyStoreService, Mockito.times(1)).readPrivateKeyFromKeystore(address.value());
            Mockito.verify(walletRepository, Mockito.never()).retrievePublicKeyByAddress(address.value());
        }
    }

    @Test
    void testSign_keystoreFailure_throwsCryptographicException() {
        // Given
        AddressModel address = WalletTestData.ADDRESS_ALPHA;

        // When
        try (MockedStatic<KeyPairUtility> keyPairUtilityMock = Mockito.mockStatic(KeyPairUtility.class)) {

            Mockito.when(keyStoreService.readPrivateKeyFromKeystore(address.value())).thenThrow(CryptographicException.class);

            // Then
            Assertions.assertThrows(CryptographicException.class, () -> walletService.sign(address, "message"), "Exception should be thrown in the event of keystore failure.");

            Mockito.verify(keyStoreService, Mockito.times(1)).readPrivateKeyFromKeystore(address.value());
            Mockito.verify(walletRepository, Mockito.never()).retrievePublicKeyByAddress(address.value());
        }
    }

    @Test
    void testSign_signFailure_throwsCryptographicException() {
        // Given
        AddressModel address = WalletTestData.ADDRESS_ALPHA;

        KeyPair keyPair = WalletTestData.KEYPAIR_ALPHA;

        // When
        try (MockedStatic<KeyPairUtility> keyPairUtilityMock = Mockito.mockStatic(KeyPairUtility.class)) {

            Mockito.when(keyStoreService.readPrivateKeyFromKeystore(address.value())).thenReturn(keyPair.getPrivate());

            keyPairUtilityMock.when(() -> KeyPairUtility.sign(Mockito.any(), Mockito.any())).thenThrow(CryptographicException.class);

            // Then
            Assertions.assertThrows(CryptographicException.class, () -> walletService.sign(address, "message"), "Exception should be thrown in the event of signing failure.");

            Mockito.verify(keyStoreService, Mockito.times(1)).readPrivateKeyFromKeystore(address.value());
        }
    }

    @Test
    void testVerifySignature_returnsTrue() {
        // Given
        KeyPair keyPair = WalletTestData.KEYPAIR_ALPHA;

        // When
        try (MockedStatic<KeyPairUtility> keyPairUtilityMock = Mockito.mockStatic(KeyPairUtility.class)) {
            
            keyPairUtilityMock.when(() -> KeyPairUtility.loadPublicKey(keyPair.getPublic().getEncoded())).thenReturn(keyPair.getPublic());
            keyPairUtilityMock.when(() -> KeyPairUtility.verifySignature(keyPair.getPublic(), "message", "signature")).thenReturn(true);

            boolean isValid = walletService.verifySignature(keyPair.getPublic().getEncoded(), "message", "signature");

            // Then
            Assertions.assertTrue(isValid);
        }
    }

    @Test
    void testVerifySignature_returnsFalse() {
        // Given
        KeyPair keyPair = WalletTestData.KEYPAIR_ALPHA;

        // When
        try (MockedStatic<KeyPairUtility> keyPairUtilityMock = Mockito.mockStatic(KeyPairUtility.class)) {

            keyPairUtilityMock.when(() -> KeyPairUtility.loadPublicKey(keyPair.getPublic().getEncoded())).thenReturn(keyPair.getPublic());
            keyPairUtilityMock.when(() -> KeyPairUtility.verifySignature(keyPair.getPublic(), "message", "signature")).thenReturn(false);

            boolean isValid = walletService.verifySignature(keyPair.getPublic().getEncoded(), "message", "signature");

            // Then
            Assertions.assertFalse(isValid);
        }
    }

    @Test
    void testVerifySignature_loadPublicKeyFailure_throwsCryptographicException() {
        // Given
        KeyPair keyPair = WalletTestData.KEYPAIR_ALPHA;

        // When
        try (MockedStatic<KeyPairUtility> keyPairUtilityMock = Mockito.mockStatic(KeyPairUtility.class)) {

            keyPairUtilityMock.when(() -> KeyPairUtility.loadPublicKey(keyPair.getPublic().getEncoded())).thenThrow(CryptographicException.class);

            // Then
            Assertions.assertThrows(CryptographicException.class, () -> walletService.verifySignature(keyPair.getPublic().getEncoded(), "message", "signature"), "Exception should be thrown in the event of public key loading failure.");
        }
    }

    @Test
    void testVerifySignature_verificationFailure_throwsCryptographicException() {
        // Given
        KeyPair keyPair = WalletTestData.KEYPAIR_ALPHA;

        // When
        try (MockedStatic<KeyPairUtility> keyPairUtilityMock = Mockito.mockStatic(KeyPairUtility.class)) {

            keyPairUtilityMock.when(() -> KeyPairUtility.loadPublicKey(keyPair.getPublic().getEncoded())).thenReturn(keyPair.getPublic());
            keyPairUtilityMock.when(() -> KeyPairUtility.verifySignature(keyPair.getPublic(), "message", "signature")).thenThrow(CryptographicException.class);

            // Then
            Assertions.assertThrows(CryptographicException.class, () -> walletService.verifySignature(keyPair.getPublic().getEncoded(), "message", "signature"), "Exception should be thrown in the event of verification failure.");
        }
    }

    @Test
    void testGetPublicKeyEncoded_returnsEncodedPublicKey() {
        // Given
        AddressModel address = WalletTestData.ADDRESS_ALPHA;

        byte[] keyBytes = new byte[]{};

        // When
        Mockito.when(walletRepository.retrievePublicKeyByAddress(address.value())).thenReturn(keyBytes);

        // Then
        byte[] publicKeyEncoded = walletRepository.retrievePublicKeyByAddress(address.value());

        Assertions.assertArrayEquals(keyBytes, publicKeyEncoded);
    }

    @Test
    void testGetPublicKeyEncoded_addressNotFound_throwsNotFoundException() {
        // Given
        AddressModel address = WalletTestData.ADDRESS_ALPHA;

        // When
        Mockito.when(walletRepository.retrievePublicKeyByAddress(address.value())).thenThrow(NoDataFoundException.class);

        // Then
        Exception thrown = Assertions.assertThrows(NotFoundException.class, () -> walletService.getPublicKeyEncoded(address), "Exception should be thrown in the event of no wallet found using address.");
        Assertions.assertEquals("Wallet does not exist in the database: " + address.value(), thrown.getMessage());
    }

    @Test
    void testGetPublicKeyEncoded_queryFailure_throwsDataAccessException() {
        // Given
        AddressModel address = WalletTestData.ADDRESS_ALPHA;

        // When
        Mockito.when(walletRepository.retrievePublicKeyByAddress(address.value())).thenThrow(DataAccessException.class);

        // Then
        Assertions.assertThrows(DataAccessException.class, () -> walletService.getPublicKeyEncoded(address), "Exception should be thrown in the event of database query failure.");
    }
}
