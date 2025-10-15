package org.acme.wallet.repository;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jooq.tables.Wallet;
import jooq.tables.records.WalletRecord;
import org.acme.wallet.mapper.WalletMapper;
import org.acme.wallet.model.WalletModel;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 * A repository component responsible for persistence operations related to the {@link WalletModel}.
 * <p>
 * This class uses **jOOQ** (via {@link DSLContext}) for type-safe, fluent SQL construction
 * and interacts directly with the database to manage wallet data, including insertion
 * and key retrieval. It is scoped as a singleton within the Quarkus application.
 */
@ApplicationScoped
public class WalletRepository {

    private final DSLContext dslContext;

    /**
     * Constructs the WalletRepository, initializing the jOOQ DSLContext.
     * <p>
     * It uses the injected {@code AgroalDataSource} (Quarkus's default JDBC connection pool)
     * and sets the {@code SQLDialect} to PostgreSQL, ensuring all jOOQ operations are
     * compatible with the target database.
     *
     * @param agroalDataSource The managed connection pool provided by Quarkus/Agroal.
     */
    @Inject
    public WalletRepository(AgroalDataSource agroalDataSource) {
        this.dslContext = DSL.using(agroalDataSource, SQLDialect.POSTGRES);
    }

    /**
     * Inserts a new wallet record into the database.
     * <p>
     * The {@link WalletModel} is first mapped to a jOOQ-compatible {@code WalletRecord}
     * by the {@link WalletMapper}, and then the record is persisted using a jOOQ INSERT statement.
     * The database is responsible for generating the primary ID field.
     *
     * @param wallet The fully initialized {@link WalletModel} to be inserted.
     * @throws org.jooq.exception.DataAccessException If the insertion fails due to a database constraint violation or connection issue.
     */
    public void insert(WalletModel wallet) {
        WalletRecord toInsert = WalletMapper.INSTANCE.modelToRecord(wallet);

        dslContext
                .insertInto(Wallet.WALLET)
                .set(toInsert)
                .execute();
    }

    /**
     * Retrieves the raw encoded public key bytes associated with a specific wallet address.
     * <p>
     * This method executes a simple selective query based on the unique wallet address.
     *
     * @param address The unique public key hash (wallet address) to look up.
     * @return The public key encoded as a byte array (e.g., X.509 format).
     * @throws org.jooq.exception.NoDataFoundException If no wallet is found for the given address.
     * @throws org.jooq.exception.DataAccessException For general database errors.
     */
    public byte[] retrievePublicKeyByAddress(String address) {
        return dslContext
                .select(Wallet.WALLET.PUBLIC_KEY_ENCODED)
                .from(Wallet.WALLET)
                .where(Wallet.WALLET.ADDRESS.eq(address))
                .fetchSingle(Wallet.WALLET.PUBLIC_KEY_ENCODED);
    }

    /**
     * Checks for the existence of a wallet record based on its unique address.
     * <p>
     * This performs an optimized query using {@code dslContext.fetchExists} which is highly efficient
     * for presence checks, typically translating to a {@code SELECT 1... LIMIT 1} query.
     *
     * @param address The unique wallet address to check.
     * @return {@code true} if a wallet with the given address exists in the database, {@code false} otherwise.
     * @throws org.jooq.exception.DataAccessException For general database errors.
     */
    public boolean exists(String address) {
        return dslContext
                .fetchExists(dslContext
                        .selectOne()
                        .from(Wallet.WALLET)
                        .where(Wallet.WALLET.ADDRESS.eq(address)));
    }
}
