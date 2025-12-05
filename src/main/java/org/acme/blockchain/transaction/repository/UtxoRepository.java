package org.acme.blockchain.transaction.repository;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jooq.tables.Utxo;
import jooq.tables.records.UtxoRecord;
import org.acme.blockchain.transaction.mapper.UtxoMapper;
import org.acme.blockchain.transaction.model.UtxoId;
import org.acme.blockchain.transaction.model.UtxoModel;
import org.jooq.DSLContext;
import org.jooq.Row2;
import org.jooq.SQLDialect;
import org.jooq.exception.NoDataFoundException;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.row;

@ApplicationScoped
public class UtxoRepository {

    private final DSLContext dslContext;

    @Inject
    public UtxoRepository(AgroalDataSource agroalDataSource) {
        this.dslContext = DSL.using(agroalDataSource, SQLDialect.POSTGRES);
    }

    public void batchInsert(List<UtxoModel> utxos) {
        List<UtxoRecord> toInsert = utxos.stream().map(UtxoMapper.INSTANCE::modelToRecord).collect(Collectors.toList());

        dslContext.insertInto(Utxo.UTXO)
                .set(toInsert)
                .execute();
    }

    public List<UtxoModel> retrieveUtxosById(List<UtxoId> ids) {
        List<Row2<String, String>> rows = ids.stream()
                .map(id -> row(id.getTransactionHashId().value(), id.getOutputIndex().getIndex()))
                .toList();

        List<UtxoRecord> records = dslContext
                .selectFrom(Utxo.UTXO)
                .where(row(Utxo.UTXO.TRANSACTION_HASH_ID, Utxo.UTXO.OUTPUT_INDEX).in(rows))
                .fetch();

        if (records.isEmpty()) {
            throw new NoDataFoundException();
        }

        return records.stream().map(UtxoMapper.INSTANCE::recordToModel).toList();
    }

    public List<UtxoModel> retrieveUtxoByTransactionHashId(String hashId) {
        List<UtxoRecord> records = dslContext
                .selectFrom(Utxo.UTXO)
                .where(Utxo.UTXO.TRANSACTION_HASH_ID.eq(hashId))
                .orderBy(Utxo.UTXO.OUTPUT_INDEX.asc())
                .fetch();

        if (records.isEmpty()) {
            throw new NotFoundException();
        }

        return records.stream().map(UtxoMapper.INSTANCE::recordToModel).toList();
    }

    public boolean isSpent(UtxoId id) {
        return dslContext
                .fetchExists(dslContext
                        .selectOne()
                        .from(Utxo.UTXO)
                        .where(Utxo.UTXO.TRANSACTION_HASH_ID.eq(id.getTransactionHashId().value()))
                        .and(Utxo.UTXO.OUTPUT_INDEX.eq(id.getOutputIndex().getIndex()))
                        .and(Utxo.UTXO.IS_SPENT.isTrue()));
    }

    public List<UtxoModel> retrieveUnspentUtxosByRecipientAddress(String recipientAddress) {
        List<UtxoRecord> utxoRecords = dslContext
                .selectFrom(Utxo.UTXO)
                .where(Utxo.UTXO.RECIPIENT_ADDRESS.eq(recipientAddress))
                .and(Utxo.UTXO.IS_SPENT.isFalse())
                .orderBy(Utxo.UTXO.CREATED_AT.asc())
                .fetch();

        return utxoRecords.stream().map(UtxoMapper.INSTANCE::recordToModel).toList();
    }

    public void updateUnspentUtxoToSpent(List<Long> utxoIds) {
        dslContext
                .update(Utxo.UTXO)
                .set(Utxo.UTXO.IS_SPENT, true)
                .where(Utxo.UTXO.ID.in(utxoIds))
                .execute();
    }
}
