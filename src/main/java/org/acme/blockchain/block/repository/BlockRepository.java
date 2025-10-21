package org.acme.blockchain.block.repository;

import org.acme.blockchain.block.mapper.BlockMapper;
import org.acme.blockchain.block.model.BlockModel;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jooq.tables.Block;
import jooq.tables.records.BlockRecord;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

@ApplicationScoped
public class BlockRepository {

    private final DSLContext dslContext;

    @Inject
    public BlockRepository(AgroalDataSource agroalDataSource) {
        this.dslContext = DSL.using(agroalDataSource, SQLDialect.POSTGRES);
    }

    public void insert(BlockModel block) {
        BlockRecord toInsert = BlockMapper.INSTANCE.modelToRecord(block);

        dslContext
                .insertInto(Block.BLOCK)
                .set(toInsert)
                .returning()
                .fetchSingle();
    }

    public BlockModel getLatestBlock() {
        BlockRecord record = dslContext
                .selectFrom(Block.BLOCK)
                .orderBy(Block.BLOCK.ID.desc())
                .limit(1)
                .fetchSingle();

        return BlockMapper.INSTANCE.recordToModel(record);
    }
}
