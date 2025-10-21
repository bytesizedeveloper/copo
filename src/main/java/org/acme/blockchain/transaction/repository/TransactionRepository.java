package org.acme.blockchain.transaction.repository;

import org.acme.blockchain.transaction.mapper.TransactionMapper;
import org.acme.blockchain.transaction.model.TransactionModel;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jooq.tables.Transaction;
import jooq.tables.records.TransactionRecord;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository class responsible for all database interactions related to the Transaction model.
 * <p>
 * This class uses jOOQ's {@link DSLContext} to perform strongly-typed data access operations,
 * primarily focusing on the persistence of finalized transactions before they are added to a block.
 */
@ApplicationScoped
public class TransactionRepository {

    /**
     * The jOOQ context used to build and execute SQL queries against the database.
     */
    private final DSLContext dslContext;

    /**
     * Constructs the TransactionRepository, initializing the jOOQ DSLContext.
     *
     * @param agroalDataSource The Agroal data source provided by the Quarkus application environment
     * for database connection pooling.
     */
    @Inject
    public TransactionRepository(AgroalDataSource agroalDataSource) {
        this.dslContext = DSL.using(agroalDataSource, SQLDialect.POSTGRES);
    }

    /**
     * Inserts a new transaction record into the database using jOOQ's DML API.
     * <p>
     * This method maps the business {@link TransactionModel} to a database {@code TransactionRecord}
     * and executes a type-safe SQL {@code INSERT} operation. It is typically called after a transaction
     * has been fully finalized and signed.
     *
     * @param transaction The {@link TransactionModel} containing all finalized data (hash, signature, inputs, outputs, etc.)
     * to be persisted as a new record.
     * @return void
     */
    public void batchInsert(List<TransactionModel> transactions) {
        List<TransactionRecord> toInsert = transactions.stream().map(TransactionMapper.INSTANCE::modelToRecord).collect(Collectors.toList());

        dslContext.insertInto(Transaction.TRANSACTION)
                .set(toInsert)
                .execute();
    }
}
