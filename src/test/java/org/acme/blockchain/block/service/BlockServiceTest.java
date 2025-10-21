package org.acme.blockchain.block.service;

import org.acme.blockchain.block.model.BlockModel;
import org.acme.blockchain.block.repository.BlockRepository;
import org.acme.blockchain.test_common.test_data.BlockTestData;
import org.jooq.exception.NoDataFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BlockServiceTest {

    @Mock
    BlockRepository blockRepository;

    @InjectMocks
    BlockService blockService;

    @Test
    void testGetLatestBlock_returnsLatest() {
        // Given
        BlockModel block = BlockTestData.getBlock();

        // When
        Mockito.when(blockRepository.getLatestBlock()).thenReturn(block);

        // Then
        blockService.getLatestBlock();

        Mockito.verify(blockRepository, Mockito.times(1)).getLatestBlock();
    }

    @Test
    void testGetLatestBlock_noLatest() {
        // Given
        BlockModel genesis = BlockTestData.getGenesisBlock();

        // When
        Mockito.when(blockRepository.getLatestBlock()).thenThrow(NoDataFoundException.class);

        // Then
        BlockModel latest = blockService.getLatestBlock();

        Assertions.assertNotNull(latest.getHashId());
        Assertions.assertEquals(64, latest.getHashId().length());
        Assertions.assertEquals(genesis.getPreviousHashId(), latest.getPreviousHashId());
        Assertions.assertEquals(genesis.getTransactions(), latest.getTransactions());
        Assertions.assertEquals(genesis.getHeight(), latest.getHeight());
        Assertions.assertEquals(genesis.getNonce(), latest.getNonce());
        Assertions.assertEquals(genesis.getDifficulty(), latest.getDifficulty());
        Assertions.assertEquals(genesis.getReward(), latest.getReward());
        Assertions.assertNotNull(latest.getCreatedAt());
        Assertions.assertNotNull(latest.getMinedAt());

        Mockito.verify(blockRepository, Mockito.times(1)).getLatestBlock();
    }
}
