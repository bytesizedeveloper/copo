package org.acme.blockchain.transaction.repository;

import io.agroal.api.AgroalDataSource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jooq.tables.Wallet;
import org.acme.blockchain.test_common.resource.PostgresWithFlywayTestResource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;

@QuarkusTest
@QuarkusTestResource(PostgresWithFlywayTestResource.class)
public class TransactionRepositoryTest {

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    DSLContext dslContext;

    @Inject
    AgroalDataSource agroalDataSource;

    @BeforeEach
    public void setup() {
        this.dslContext = DSL.using(agroalDataSource, SQLDialect.POSTGRES);
        dslContext.truncate(Wallet.WALLET).cascade().execute();
    }
}
