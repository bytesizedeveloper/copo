package org.acme.wallet.repository;

import io.agroal.api.AgroalDataSource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jooq.tables.Wallet;
import org.acme.test_common.resource.PostgresWithFlywayTestResource;
import org.acme.test_common.test_data.WalletTestData;
import org.acme.wallet.model.WalletModel;
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
    public void setup() {
        this.dslContext = DSL.using(agroalDataSource, SQLDialect.POSTGRES);
        dslContext.truncate(Wallet.WALLET).cascade().execute();
    }

    @Test
    public void testInsert() {
        // Given
        WalletModel wallet = WalletTestData.getWallet();

        // When
        walletRepository.insert(wallet);

        // Then
        Assertions.assertTrue(walletRepository.exists(wallet.getAddress()));
    }

    @Test
    public void testRetrievePublicKeyByAddress_addressFound_returnsPublicKey() {
        // Given
        WalletModel wallet = WalletTestData.getWallet();

        walletRepository.insert(wallet);

        // When
        byte[] publicKey = walletRepository.retrievePublicKeyByAddress(wallet.getAddress());

        // Then
        Assertions.assertArrayEquals(wallet.getPublicKeyEncoded(), publicKey);
    }

    @Test
    public void testRetrievePublicKeyByAddress_addressNotFound_throwsNoDataFoundException() {
        // Given
        String address = WalletTestData.ADDRESS_ALPHA;

        // When & Then
        Assertions.assertThrows(NoDataFoundException.class, () -> walletRepository.retrievePublicKeyByAddress(address));
    }

    @Test
    public void testExists_addressFound_returnsTrue() {
        // Given
        WalletModel wallet = WalletTestData.getWallet();

        walletRepository.insert(wallet);

        // When
        boolean exists = walletRepository.exists(wallet.getAddress());

        // Then
        Assertions.assertTrue(exists);
    }

    @Test
    public void testExists_addressNotFound_returnsFalse() {
        // Given
        String address = WalletTestData.ADDRESS_ALPHA;

        // When
        boolean exists = walletRepository.exists(address);

        // Then
        Assertions.assertFalse(exists);
    }
}
