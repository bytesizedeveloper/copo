package org.acme.blockchain.block.service;

import org.acme.blockchain.block.model.BlockModel;
import org.acme.blockchain.common.service.DifficultyService;
import org.acme.blockchain.common.service.RewardService;
import org.acme.blockchain.common.service.TransactionCacheService;
import org.acme.blockchain.common.utility.HashUtility;
import org.acme.blockchain.common.utility.TimestampUtility;
import org.acme.blockchain.common.utility.WalletUtility;
import org.acme.blockchain.network.TempNetwork;
import org.acme.blockchain.test_common.test_data.BlockTestData;
import org.acme.blockchain.test_common.test_data.TransactionTestData;
import org.acme.blockchain.test_common.test_data.WalletTestData;
import org.acme.blockchain.transaction.model.CoinModel;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.service.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class MinerServiceTest {

    @Mock
    MinerCacheService minerCache;

    @Mock
    TransactionCacheService transactionCache;

    @Mock
    DifficultyService difficultyService;

    @Mock
    RewardService rewardService;

    @Mock
    BlockService blockService;

    @Mock
    TransactionService transactionService;

    @Mock
    TempNetwork tempNetwork;

    @InjectMocks
    MinerService minerService;

    @Test
    void testStartMining_updatesMinerCache() {
        // Given - miner not active
        String address = WalletTestData.ADDRESS_ALPHA;

        try (MockedStatic<WalletUtility> walletUtilityMock = Mockito.mockStatic(WalletUtility.class)) {
            // When
            walletUtilityMock.when(() -> WalletUtility.isValid(address)).thenReturn(true);
            Mockito.when(minerCache.contains(address)).thenReturn(false);

            // Then
            boolean result = minerService.startMining(address);

            Assertions.assertTrue(result);
            Mockito.verify(minerCache, Mockito.times(1)).contains(address);
            Mockito.verify(minerCache, Mockito.times(1)).add(address);
        }
    }

    @Test
    void testStartMining_alreadyMining() {
        // Given - miner is already active
        String address = WalletTestData.ADDRESS_ALPHA;

        try (MockedStatic<WalletUtility> walletUtilityMock = Mockito.mockStatic(WalletUtility.class)) {
            // When
            walletUtilityMock.when(() -> WalletUtility.isValid(address)).thenReturn(true);
            Mockito.when(minerCache.contains(address)).thenReturn(true);

            // Then
            boolean result = minerService.startMining(address);

            Assertions.assertFalse(result);
            Mockito.verify(minerCache, Mockito.times(1)).contains(address);
            Mockito.verify(minerCache, Mockito.never()).add(address);
        }
    }

    @Test
    void testStartMining_invalidAddress() {
        // Given
        String address = WalletTestData.ADDRESS_ALPHA;

        try (MockedStatic<WalletUtility> walletUtilityMock = Mockito.mockStatic(WalletUtility.class)) {
            // When
            walletUtilityMock.when(() -> WalletUtility.isValid(address)).thenReturn(false);

            // Then
            Exception thrown = Assertions.assertThrows(Exception.class, () -> minerService.startMining(address), "The invalid address must throw a IllegalStateException.");
            Assertions.assertInstanceOf(IllegalStateException.class, thrown);

            Mockito.verify(minerCache, Mockito.never()).contains(address);
            Mockito.verify(minerCache, Mockito.never()).add(address);
        }
    }

    @Test
    void testStopMining_updatesMinerCache() {
        // Given - miner is already active
        String address = WalletTestData.ADDRESS_ALPHA;

        try (MockedStatic<WalletUtility> walletUtilityMock = Mockito.mockStatic(WalletUtility.class)) {
            // When
            walletUtilityMock.when(() -> WalletUtility.isValid(address)).thenReturn(true);
            Mockito.when(minerCache.contains(address)).thenReturn(true);

            // Then
            boolean result = minerService.stopMining(address);

            Assertions.assertTrue(result);
            Mockito.verify(minerCache, Mockito.times(1)).contains(address);
            Mockito.verify(minerCache, Mockito.times(1)).remove(address);
        }
    }

    @Test
    void testStopMining_alreadyStopped() {
        // Given - miner is not active
        String address = WalletTestData.ADDRESS_ALPHA;

        try (MockedStatic<WalletUtility> walletUtilityMock = Mockito.mockStatic(WalletUtility.class)) {
            // When
            walletUtilityMock.when(() -> WalletUtility.isValid(address)).thenReturn(true);
            Mockito.when(minerCache.contains(address)).thenReturn(false);

            // Then
            boolean result = minerService.stopMining(address);

            Assertions.assertFalse(result);
            Mockito.verify(minerCache, Mockito.times(1)).contains(address);
            Mockito.verify(minerCache, Mockito.never()).remove(address);
        }
    }

    @Test
    void testStopMining_invalidAddress_throwsIllegalStateException() {
        // Given
        String address = WalletTestData.ADDRESS_ALPHA;

        try (MockedStatic<WalletUtility> walletUtilityMock = Mockito.mockStatic(WalletUtility.class)) {
            // When
            walletUtilityMock.when(() -> WalletUtility.isValid(address)).thenReturn(false);

            // Then
            Exception thrown = Assertions.assertThrows(Exception.class, () -> minerService.stopMining(address), "The invalid address must throw a IllegalStateException.");
            Assertions.assertInstanceOf(IllegalStateException.class, thrown);

            Mockito.verify(minerCache, Mockito.never()).contains(address);
            Mockito.verify(minerCache, Mockito.never()).remove(address);
        }
    }

    @Test
    void testPulse_singleMiner_blockMinedAndPublished() {
        // Given
        String address = WalletTestData.ADDRESS_ALPHA;
        Set<String> activeMiners = Set.of(address);

        BlockModel genesisBlock = BlockTestData.getGenesisBlock();
        int difficulty = BlockTestData.DIFFICULTY;
        CoinModel rewardAmount = TransactionTestData.REWARD;

        TransactionModel transaction = TransactionTestData.getTransactionPostInitialise();
        List<TransactionModel> readyToMine = List.of(transaction);

        String hashId = BlockTestData.VALID_HASH_ID_1;

        // When
        Mockito.when(minerCache.getIsMining()).thenReturn(activeMiners);

        Mockito.when(blockService.getLatestBlock()).thenReturn(genesisBlock);
        Mockito.when(difficultyService.calculateDifficulty()).thenReturn(difficulty);
        Mockito.when(rewardService.determineRewardAmount()).thenReturn(rewardAmount);

        Mockito.when(transactionCache.getReadyToMine()).thenReturn(readyToMine);

        Mockito.when(transactionService.createReward(address, rewardAmount)).thenReturn(TransactionTestData.getAlphaRewardPostInitialise());

        try (MockedStatic<HashUtility> hashUtilityMock = Mockito.mockStatic(HashUtility.class);
             MockedStatic<TimestampUtility> timestampUtilityMock = Mockito.mockStatic(TimestampUtility.class)) {

            hashUtilityMock.when(() -> HashUtility.calculateSHA256d(Mockito.any(String.class))).thenReturn(hashId);
            timestampUtilityMock.when(TimestampUtility::getOffsetDateTimeNow).thenReturn(BlockTestData.NOW);

            // Then
            minerService.pulse();

            Mockito.verify(minerCache, Mockito.times(1)).getIsMining();
            Mockito.verify(minerCache, Mockito.times(1)).setIsPulseMined(false);

            Mockito.verify(blockService, Mockito.times(1)).getLatestBlock();
            Mockito.verify(difficultyService, Mockito.times(1)).calculateDifficulty();
            Mockito.verify(rewardService, Mockito.times(1)).determineRewardAmount();

            Mockito.verify(transactionCache, Mockito.times(1)).getReadyToMine();
            Mockito.verify(minerCache, Mockito.times(1)).remove(address);

            Mockito.verify(transactionService, Mockito.times(1)).createReward(address, rewardAmount);

            Mockito.verify(minerCache, Mockito.atLeast(2)).getIsPulseMined();
            Mockito.verify(minerCache, Mockito.times(1)).setIsPulseMined(true);

            Mockito.verify(tempNetwork, Mockito.times(1)).broadcast(Mockito.any(BlockModel.class));
            Mockito.verify(minerCache, Mockito.times(1)).add(address);
        }
    }

    @Test
    void testPulse_multipleMiners_blocksMinedAndPublished() {
        // Given
        String addressAlpha = WalletTestData.ADDRESS_ALPHA;
        String addressBeta = WalletTestData.ADDRESS_BETA;
        Set<String> activeMiners = Set.of(addressAlpha, addressBeta);

        BlockModel genesisBlock = BlockTestData.getGenesisBlock();
        int difficulty = BlockTestData.DIFFICULTY;
        CoinModel rewardAmount = TransactionTestData.REWARD;

        TransactionModel transaction = TransactionTestData.getTransactionPostInitialise();
        List<TransactionModel> readyToMine = List.of(transaction);

        String validHashId = BlockTestData.VALID_HASH_ID_1;

        // When
        Mockito.when(minerCache.getIsMining()).thenReturn(activeMiners);

        Mockito.when(blockService.getLatestBlock()).thenReturn(genesisBlock);
        Mockito.when(difficultyService.calculateDifficulty()).thenReturn(difficulty);
        Mockito.when(rewardService.determineRewardAmount()).thenReturn(rewardAmount);

        Mockito.when(transactionCache.getReadyToMine()).thenReturn(readyToMine);

        Mockito.when(transactionService.createReward(addressAlpha, rewardAmount)).thenReturn(TransactionTestData.getAlphaRewardPostInitialise());
        Mockito.when(transactionService.createReward(addressBeta, rewardAmount)).thenReturn(TransactionTestData.getBetaRewardPostInitialise());

        try (MockedStatic<HashUtility> hashUtilityMock = Mockito.mockStatic(HashUtility.class);
             MockedStatic<TimestampUtility> timestampUtilityMock = Mockito.mockStatic(TimestampUtility.class)) {

            hashUtilityMock.when(() -> HashUtility.calculateSHA256d(Mockito.any(String.class)))
                    .thenReturn(validHashId)
                    .thenReturn(validHashId);
            timestampUtilityMock.when(TimestampUtility::getOffsetDateTimeNow).thenReturn(BlockTestData.NOW);

            // Then
            minerService.pulse();

            Mockito.verify(minerCache, Mockito.times(1)).getIsMining();
            Mockito.verify(minerCache, Mockito.times(1)).setIsPulseMined(false);

            Mockito.verify(blockService, Mockito.times(1)).getLatestBlock();
            Mockito.verify(difficultyService, Mockito.times(1)).calculateDifficulty();
            Mockito.verify(rewardService, Mockito.times(1)).determineRewardAmount();

            Mockito.verify(transactionCache, Mockito.times(1)).getReadyToMine();
            Mockito.verify(minerCache, Mockito.times(2)).remove(Mockito.any(String.class));

            Mockito.verify(transactionService, Mockito.times(2)).createReward(Mockito.any(String.class), Mockito.any(CoinModel.class));

            Mockito.verify(minerCache, Mockito.atLeast(2)).getIsPulseMined();
            Mockito.verify(minerCache, Mockito.times(2)).setIsPulseMined(true);

            Mockito.verify(tempNetwork, Mockito.times(2)).broadcast(Mockito.any(BlockModel.class));
            Mockito.verify(minerCache, Mockito.times(2)).add(Mockito.any(String.class));
        }
    }

    @Test
    void testPulse_singleMiner_exceptionThrown() {
        // Given
        String address = WalletTestData.ADDRESS_ALPHA;
        Set<String> activeMiners = Set.of(address);

        BlockModel genesisBlock = BlockTestData.getGenesisBlock();
        int difficulty = BlockTestData.DIFFICULTY;
        CoinModel rewardAmount = TransactionTestData.REWARD;

        TransactionModel transaction = TransactionTestData.getTransactionPostInitialise();
        List<TransactionModel> readyToMine = List.of(transaction);

        // When
        Mockito.when(minerCache.getIsMining()).thenReturn(activeMiners);

        Mockito.when(blockService.getLatestBlock()).thenReturn(genesisBlock);
        Mockito.when(difficultyService.calculateDifficulty()).thenReturn(difficulty);
        Mockito.when(rewardService.determineRewardAmount()).thenReturn(rewardAmount);

        Mockito.when(transactionCache.getReadyToMine()).thenReturn(readyToMine);

        Mockito.when(transactionService.createReward(address, rewardAmount)).thenThrow(RuntimeException.class);

        // Then
        minerService.pulse();

        Mockito.verify(minerCache, Mockito.times(1)).getIsMining();
        Mockito.verify(minerCache, Mockito.times(1)).setIsPulseMined(false);

        Mockito.verify(blockService, Mockito.times(1)).getLatestBlock();
        Mockito.verify(difficultyService, Mockito.times(1)).calculateDifficulty();
        Mockito.verify(rewardService, Mockito.times(1)).determineRewardAmount();

        Mockito.verify(transactionCache, Mockito.times(1)).getReadyToMine();
        Mockito.verify(minerCache, Mockito.times(1)).remove(address);

        Mockito.verify(transactionService, Mockito.times(1)).createReward(address, rewardAmount);

        Mockito.verify(minerCache, Mockito.never()).getIsPulseMined();
        Mockito.verify(minerCache, Mockito.never()).setIsPulseMined(true);

        Mockito.verify(tempNetwork, Mockito.never()).broadcast(Mockito.any(BlockModel.class));
        Mockito.verify(minerCache, Mockito.times(1)).add(address);
    }

    @Test
    void testPulse_noMiner_blockNotMinedAndPublished() {
        // Given
        Set<String> activeMiners = Set.of();

        // When
        Mockito.when(minerCache.getIsMining()).thenReturn(activeMiners);

        // Then
        minerService.pulse();

        Mockito.verify(minerCache, Mockito.times(1)).getIsMining();
        Mockito.verify(minerCache, Mockito.never()).setIsPulseMined(false);

        Mockito.verify(blockService, Mockito.never()).getLatestBlock();
        Mockito.verify(difficultyService, Mockito.never()).calculateDifficulty();
        Mockito.verify(rewardService, Mockito.never()).determineRewardAmount();

        Mockito.verify(transactionCache, Mockito.never()).getReadyToMine();
        Mockito.verify(minerCache, Mockito.never()).remove(Mockito.any(String.class));

        Mockito.verify(transactionService, Mockito.never()).createReward(Mockito.any(String.class), Mockito.any(CoinModel.class));

        Mockito.verify(minerCache, Mockito.never()).getIsPulseMined();
        Mockito.verify(minerCache, Mockito.never()).setIsPulseMined(true);

        Mockito.verify(tempNetwork, Mockito.never()).broadcast(Mockito.any(BlockModel.class));
        Mockito.verify(minerCache, Mockito.never()).add(Mockito.any(String.class));
    }
}
