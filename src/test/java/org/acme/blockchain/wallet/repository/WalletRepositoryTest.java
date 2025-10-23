package org.acme.blockchain.wallet.repository;

import io.agroal.api.AgroalDataSource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jooq.tables.Wallet;
import org.acme.blockchain.test_common.resource.PostgresWithFlywayTestResource;
import org.acme.blockchain.test_common.test_data.WalletTestData;
import org.acme.blockchain.wallet.model.WalletModel;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.NoDataFoundException;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(PostgresWithFlywayTestResource.class)
public class WalletRepositoryTest {

    @Inject
    WalletRepository walletRepository;

    @Inject
    DSLContext dslContext;

    @Inject
    AgroalDataSource agroalDataSource;

    @BeforeEach
    void setup() {
        this.dslContext = DSL.using(agroalDataSource, SQLDialect.POSTGRES);
        dslContext.truncate(Wallet.WALLET).cascade().execute();
    }

    @Test
    void testInsert() {
        // Given
        WalletModel toInsert = WalletTestData.getWalletAlpha();

        // When
        walletRepository.insert(toInsert);

        // Then
        Assertions.assertTrue(walletRepository.exists(toInsert.address().value()));
    }

    @Test
    void testRetrieveWalletByAddress_addressFound_returnsPublicKey() {
        // Given
        WalletModel toInsert = WalletTestData.getWalletAlpha();

        walletRepository.insert(toInsert);

        // When
        WalletModel wallet = walletRepository.retrieveWalletByAddress(toInsert.address().value());

        // Then
        Assertions.assertNull(wallet.keyPair());
        Assertions.assertEquals(toInsert.address(), wallet.address());
        Assertions.assertArrayEquals(toInsert.publicKeyEncoded(), wallet.publicKeyEncoded());
        Assertions.assertNotNull(wallet.createdAt());
    }

    @Test
    void testRetrieveWalletByAddress_addressNotFound_throwsNoDataFoundException() {
        // Given
        String address = WalletTestData.ADDRESS_ALPHA.value();

        // When & Then
        Assertions.assertThrows(NoDataFoundException.class, () -> walletRepository.retrieveWalletByAddress(address));
    }

    @Test
    void testRetrievePublicKeyByAddress_addressFound_returnsPublicKey() {
        // Given
        WalletModel wallet = WalletTestData.getWalletAlpha();

        walletRepository.insert(wallet);

        // When
        byte[] publicKey = walletRepository.retrievePublicKeyByAddress(wallet.address().value());

        // Then
        Assertions.assertArrayEquals(wallet.publicKeyEncoded(), publicKey);
    }

    @Test
    void testRetrievePublicKeyByAddress_addressNotFound_throwsNoDataFoundException() {
        // Given
        String address = WalletTestData.ADDRESS_ALPHA.value();

        // When & Then
        Assertions.assertThrows(NoDataFoundException.class, () -> walletRepository.retrievePublicKeyByAddress(address));
    }

    @Test
    void testExists_addressFound_returnsTrue() {
        // Given
        WalletModel wallet = WalletTestData.getWalletAlpha();

        walletRepository.insert(wallet);

        // When
        boolean exists = walletRepository.exists(wallet.address().value());

        // Then
        Assertions.assertTrue(exists);
    }

    @Test
    void testExists_addressNotFound_returnsFalse() {
        // Given
        String address = WalletTestData.ADDRESS_ALPHA.value();

        // When
        boolean exists = walletRepository.exists(address);

        // Then
        Assertions.assertFalse(exists);
    }
}
