package org.acme.blockchain.wallet.repository;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jooq.tables.Wallet;
import jooq.tables.records.WalletRecord;
import org.acme.blockchain.wallet.mapper.WalletMapper;
import org.acme.blockchain.wallet.model.WalletModel;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

@ApplicationScoped
public class WalletRepository {

    private final DSLContext dslContext;

    @Inject
    public WalletRepository(AgroalDataSource agroalDataSource) {
        this.dslContext = DSL.using(agroalDataSource, SQLDialect.POSTGRES);
    }

    public void insert(WalletModel wallet) {
        WalletRecord toInsert = WalletMapper.INSTANCE.modelToRecord(wallet);

        dslContext
                .insertInto(Wallet.WALLET)
                .set(toInsert)
                .execute();
    }

    public WalletModel retrieveWalletByAddress(String address) {
        WalletRecord record = dslContext
                .selectFrom(Wallet.WALLET)
                .where(Wallet.WALLET.ADDRESS.eq(address))
                .fetchSingle();

        return WalletMapper.INSTANCE.recordToModel(record);
    }

    public byte[] retrievePublicKeyByAddress(String address) {
        return dslContext
                .select(Wallet.WALLET.PUBLIC_KEY_ENCODED)
                .from(Wallet.WALLET)
                .where(Wallet.WALLET.ADDRESS.eq(address))
                .fetchSingle(Wallet.WALLET.PUBLIC_KEY_ENCODED);
    }

    public boolean exists(String address) {
        return dslContext
                .fetchExists(dslContext
                        .selectOne()
                        .from(Wallet.WALLET)
                        .where(Wallet.WALLET.ADDRESS.eq(address)));
    }
}
