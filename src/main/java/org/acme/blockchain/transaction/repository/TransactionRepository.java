package org.acme.blockchain.transaction.repository;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jooq.tables.Transaction;
import jooq.tables.records.TransactionRecord;
import org.acme.blockchain.transaction.mapper.TransactionMapper;
import org.acme.blockchain.transaction.model.RewardModel;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransferModel;
import org.acme.blockchain.transaction.model.enumeration.TransactionType;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TransactionRepository {

    private final DSLContext dslContext;

    @Inject
    public TransactionRepository(AgroalDataSource agroalDataSource) {
        this.dslContext = DSL.using(agroalDataSource, SQLDialect.POSTGRES);
    }

    public void insert(RewardModel reward) {
        TransactionRecord toInsert = TransactionMapper.INSTANCE.modelToRecord(reward);

        dslContext.insertInto(Transaction.TRANSACTION)
                .set(toInsert)
                .execute();
    }

    public void batchInsert(List<TransferModel> transfers) {
        List<TransactionRecord> toInsert = transfers.stream()
                .map(TransactionMapper.INSTANCE::modelToRecord).collect(Collectors.toList());

        dslContext.insertInto(Transaction.TRANSACTION)
                .set(toInsert)
                .execute();
    }

    public TransactionModel retrieveTransactionByHashId(String hashId) {
        TransactionRecord record = dslContext
                .selectFrom(Transaction.TRANSACTION)
                .where(Transaction.TRANSACTION.HASH_ID.eq(hashId))
                .fetchSingle();

        if (TransactionType.TRANSFER.getType().equals(record.getType())) {
            return TransactionMapper.INSTANCE.recordToTransferModel(record);
        } else {
            return TransactionMapper.INSTANCE.recordToRewardModel(record);
        }
    }
}
