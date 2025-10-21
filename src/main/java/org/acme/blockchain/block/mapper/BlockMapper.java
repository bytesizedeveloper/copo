package org.acme.blockchain.block.mapper;

import org.acme.blockchain.block.api.contract.BlockResponse;
import org.acme.blockchain.block.model.BlockModel;
import org.acme.blockchain.transaction.mapper.CoinMapper;
import org.acme.blockchain.transaction.mapper.TransactionMapper;
import jooq.tables.records.BlockRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mapper interface for converting between different representations of a Block.
 * <p>
 * This interface handles conversions between the core domain model ({@code BlockModel}),
 * the API response format ({@code BlockResponse}), and the persistent database format ({@code BlockRecord}).
 * It utilizes {@code CoinMapper} and {@code TransactionMapper} for mapping nested objects.
 * </p>
 */
@Mapper(uses = {CoinMapper.class, TransactionMapper.class})
public interface BlockMapper {

    /**
     * Singleton instance of the BlockMapper, initialized by MapStruct.
     */
    BlockMapper INSTANCE = Mappers.getMapper(BlockMapper.class);

    /**
     * Converts the core domain model of a block to its API response format.
     *
     * @param blockModel The internal domain model of the block.
     * @return The data transfer object (DTO) used for external API responses.
     */
    BlockResponse modelToResponse(BlockModel blockModel);

    /**
     * Converts the core domain model of a block to its database record representation.
     * <p>
     * The target field 'id' (which is typically a database-generated primary key) is ignored
     * as it should not be sourced from the domain model when creating a new record.
     * </p>
     * @param blockModel The internal domain model of the block.
     * @return The database record used for persistence.
     */
    @Mapping(target = "id", ignore = true)
    BlockRecord modelToRecord(BlockModel blockModel);

    /**
     * Converts the database record of a block back to the core domain model.
     * <p>
     * The target field 'transactions' is ignored. This implies that transactions are
     * likely loaded separately (e.g., lazily or by another dedicated query) to prevent
     * large transaction lists from being loaded automatically with the block header.
     * </p>
     * @param blockRecord The database record.
     * @return The internal domain model of the block.
     */
    @Mapping(target = "transactions", ignore = true)
    BlockModel recordToModel(BlockRecord blockRecord);
}
