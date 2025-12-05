package org.acme.blockchain.transaction.service;

import org.acme.blockchain.common.service.FeeService;
import org.acme.blockchain.common.service.TransferCacheService;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.repository.UtxoRepository;
import org.acme.blockchain.wallet.service.WalletService;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    TransferCacheService cache;

    @Mock
    WalletService walletService;

    @Mock
    FeeService feeService;

    @Mock
    UtxoRepository utxoRepository;

    @Mock
    Emitter<TransactionModel> transactionEmitter;

    @InjectMocks
    TransactionService transactionService;

//    @Test
//    void testCreateTransfer_successfullyCreated_withNoChange() throws Exception {
//        // Given
//        TransferModel transfer = TransactionTestFactory.getTransferModelPreInitialise();
//
//        // When
//        Mockito.when(feeService.calculateFee()).thenReturn(TransactionTestData.FEE);
//        Mockito.when(utxoRepository.retrieveUnspentUtxosByRecipientAddress(transaction.getSenderAddress().value()))
//                .thenReturn(List.of(utxo));
//        Mockito.when(walletService.sign(Mockito.eq(transaction.getSenderAddress()), Mockito.any(String.class)))
//                .thenReturn(TransactionTestData.SIGNATURE);
//
//        // Then
//        TransactionModel result = transactionService.create(transaction);
//
//        Assertions.assertNotNull(result);
//        Assertions.assertNotNull(result.getHashId());
//        Assertions.assertEquals(transaction.getSenderAddress(), result.getSenderAddress());
//        Assertions.assertEquals(transaction.getRecipientAddress(), result.getRecipientAddress());
//        Assertions.assertEquals(transaction.getAmount(), result.getAmount());
//        Assertions.assertEquals(transaction.getFee(), result.getFee());
//        Assertions.assertEquals(TransactionType.TRANSFER, result.getType());
//        Assertions.assertNotNull(result.getCreatedAt());
//        Assertions.assertEquals(TransactionTestData.SIGNATURE, result.getSignature());
//
//        Assertions.assertEquals(1, result.getInputs().size());
//        Assertions.assertEquals(utxo.getId(), result.getInputs().get(0).getId());
//        Assertions.assertEquals(utxo.getTransactionHashId(), result.getInputs().get(0).getTransactionHashId());
//        Assertions.assertEquals(utxo.getOutputIndex(), result.getInputs().get(0).getOutputIndex());
//        Assertions.assertEquals(utxo.getRecipientAddress(), result.getInputs().get(0).getRecipientAddress());
//        Assertions.assertEquals(utxo.getAmount(), result.getInputs().get(0).getAmount());
//        Assertions.assertEquals(utxo.getCreatedAt(), result.getInputs().get(0).getCreatedAt());
//        Assertions.assertTrue(result.getInputs().get(0).isSpent());
//
//        Assertions.assertEquals(1, result.getOutputs().size());
//        Assertions.assertEquals(result.getHashId(), result.getOutputs().get(0).getTransactionHashId());
//        Assertions.assertEquals(OutputIndex.RECIPIENT.getIndex(), result.getOutputs().get(0).getOutputIndex());
//        Assertions.assertEquals(transaction.getRecipientAddress(), result.getOutputs().get(0).getRecipientAddress());
//        Assertions.assertEquals(transaction.getAmount(), result.getOutputs().get(0).getAmount());
//        Assertions.assertNotNull(result.getOutputs().get(0).getCreatedAt());
//        Assertions.assertFalse(result.getOutputs().get(0).isSpent());
//
//        Mockito.verify(transactionEmitter, Mockito.times(1)).send(Mockito.any(TransactionModel.class));
//    }
//
//    @Test
//    void testCreateTransfer_shouldCreateTransfer_withChange() throws Exception {
//        // Given - transaction requires 5 and UTXO contains 10, fee is 1
//        TransactionModel transaction = TransactionTestData.getTransferPreInitialise();
//        transaction.setAmount(new Coin(BigDecimal.valueOf(5)));
//
//        UtxoModel utxo = UtxoTestData.getInputUtxoAlpha();
//
//        Coin change = utxo.getAmount().subtract(transaction.getTotalRequired());
//
//        // When
//        Mockito.when(feeService.calculateFee()).thenReturn(TransactionTestData.FEE);
//        Mockito.when(utxoRepository.retrieveUnspentUtxosByRecipientAddress(transaction.getSenderAddress().value()))
//                .thenReturn(List.of(utxo));
//        Mockito.when(walletService.sign(Mockito.eq(transaction.getSenderAddress()), Mockito.any(String.class)))
//                .thenReturn(TransactionTestData.SIGNATURE);
//
//        // Then
//        TransactionModel result = transactionService.createTransfer(transaction);
//
//        Assertions.assertNotNull(result);
//        Assertions.assertNotNull(result.getHashId());
//        Assertions.assertEquals(transaction.getSenderAddress(), result.getSenderAddress());
//        Assertions.assertEquals(transaction.getRecipientAddress(), result.getRecipientAddress());
//        Assertions.assertEquals(transaction.getAmount(), result.getAmount());
//        Assertions.assertEquals(transaction.getFee(), result.getFee());
//        Assertions.assertEquals(TransactionType.TRANSFER, result.getType());
//        Assertions.assertNotNull(result.getCreatedAt());
//        Assertions.assertEquals(TransactionTestData.SIGNATURE, result.getSignature());
//
//        Assertions.assertEquals(1, result.getInputs().size());
//        Assertions.assertEquals(utxo.getId(), result.getInputs().get(0).getId());
//        Assertions.assertEquals(utxo.getTransactionHashId(), result.getInputs().get(0).getTransactionHashId());
//        Assertions.assertEquals(utxo.getOutputIndex(), result.getInputs().get(0).getOutputIndex());
//        Assertions.assertEquals(utxo.getRecipientAddress(), result.getInputs().get(0).getRecipientAddress());
//        Assertions.assertEquals(utxo.getAmount(), result.getInputs().get(0).getAmount());
//        Assertions.assertEquals(utxo.getCreatedAt(), result.getInputs().get(0).getCreatedAt());
//        Assertions.assertTrue(result.getInputs().get(0).isSpent());
//
//        Assertions.assertEquals(2, result.getOutputs().size());
//
//        Assertions.assertEquals(result.getHashId(), result.getOutputs().get(0).getTransactionHashId());
//        Assertions.assertEquals(OutputIndex.RECIPIENT.getIndex(), result.getOutputs().get(0).getOutputIndex());
//        Assertions.assertEquals(transaction.getRecipientAddress(), result.getOutputs().get(0).getRecipientAddress());
//        Assertions.assertEquals(transaction.getAmount(), result.getOutputs().get(0).getAmount());
//        Assertions.assertNotNull(result.getOutputs().get(0).getCreatedAt());
//        Assertions.assertFalse(result.getOutputs().get(0).isSpent());
//
//        Assertions.assertEquals(result.getHashId(), result.getOutputs().get(1).getTransactionHashId());
//        Assertions.assertEquals(OutputIndex.SENDER.getIndex(), result.getOutputs().get(1).getOutputIndex());
//        Assertions.assertEquals(transaction.getSenderAddress(), result.getOutputs().get(1).getRecipientAddress());
//        Assertions.assertEquals(change, result.getOutputs().get(1).getAmount());
//        Assertions.assertNotNull(result.getOutputs().get(1).getCreatedAt());
//        Assertions.assertFalse(result.getOutputs().get(1).isSpent());
//
//        Mockito.verify(transactionEmitter, Mockito.times(1)).send(Mockito.any(TransactionModel.class));
//    }
//
//    @Test
//    void testCreateTransfer_shouldCreateTransfer_withMultipleInputs() throws Exception {
//        // Given - transaction requires 1 and UTXOs contains .5
//        TransactionModel transaction = TransactionTestData.getTransferPreInitialise();
//
//        UtxoModel utxoAlpha = UtxoTestData.getInputUtxoAlpha();
//        utxoAlpha.setAmount(new Coin(BigDecimal.valueOf(0.5)));
//
//        UtxoModel utxoBeta = UtxoTestData.getInputUtxoBeta();
//        utxoBeta.setAmount(new Coin(BigDecimal.valueOf(0.5)));
//
//        // When
//        Mockito.when(feeService.calculateFee()).thenReturn(TransactionTestData.FEE);
//        Mockito.when(utxoRepository.retrieveUnspentUtxosByRecipientAddress(transaction.getSenderAddress().value()))
//                .thenReturn(List.of(utxoAlpha, utxoBeta));
//        Mockito.when(walletService.sign(Mockito.eq(transaction.getSenderAddress()), Mockito.any(String.class)))
//                .thenReturn(TransactionTestData.SIGNATURE);
//
//        // Then
//        TransactionModel result = transactionService.createTransfer(transaction);
//
//        Assertions.assertNotNull(result);
//        Assertions.assertNotNull(result.getHashId());
//        Assertions.assertEquals(transaction.getSenderAddress(), result.getSenderAddress());
//        Assertions.assertEquals(transaction.getRecipientAddress(), result.getRecipientAddress());
//        Assertions.assertEquals(transaction.getAmount(), result.getAmount());
//        Assertions.assertEquals(transaction.getFee(), result.getFee());
//        Assertions.assertEquals(TransactionType.TRANSFER, result.getType());
//        Assertions.assertNotNull(result.getCreatedAt());
//        Assertions.assertEquals(TransactionTestData.SIGNATURE, result.getSignature());
//
//        Assertions.assertEquals(2, result.getInputs().size());
//
//        Assertions.assertEquals(utxoAlpha.getId(), result.getInputs().get(0).getId());
//        Assertions.assertEquals(utxoAlpha.getTransactionHashId(), result.getInputs().get(0).getTransactionHashId());
//        Assertions.assertEquals(utxoAlpha.getOutputIndex(), result.getInputs().get(0).getOutputIndex());
//        Assertions.assertEquals(utxoAlpha.getRecipientAddress(), result.getInputs().get(0).getRecipientAddress());
//        Assertions.assertEquals(utxoAlpha.getAmount(), result.getInputs().get(0).getAmount());
//        Assertions.assertEquals(utxoAlpha.getCreatedAt(), result.getInputs().get(0).getCreatedAt());
//        Assertions.assertTrue(result.getInputs().get(0).isSpent());
//
//        Assertions.assertEquals(utxoBeta.getId(), result.getInputs().get(1).getId());
//        Assertions.assertEquals(utxoBeta.getTransactionHashId(), result.getInputs().get(1).getTransactionHashId());
//        Assertions.assertEquals(utxoBeta.getOutputIndex(), result.getInputs().get(1).getOutputIndex());
//        Assertions.assertEquals(utxoBeta.getRecipientAddress(), result.getInputs().get(1).getRecipientAddress());
//        Assertions.assertEquals(utxoBeta.getAmount(), result.getInputs().get(1).getAmount());
//        Assertions.assertEquals(utxoBeta.getCreatedAt(), result.getInputs().get(1).getCreatedAt());
//        Assertions.assertTrue(result.getInputs().get(1).isSpent());
//
//        Assertions.assertEquals(1, result.getOutputs().size());
//        Assertions.assertEquals(result.getHashId(), result.getOutputs().get(0).getTransactionHashId());
//        Assertions.assertEquals(OutputIndex.RECIPIENT.getIndex(), result.getOutputs().get(0).getOutputIndex());
//        Assertions.assertEquals(transaction.getRecipientAddress(), result.getOutputs().get(0).getRecipientAddress());
//        Assertions.assertEquals(transaction.getAmount(), result.getOutputs().get(0).getAmount());
//        Assertions.assertNotNull(result.getOutputs().get(0).getCreatedAt());
//        Assertions.assertFalse(result.getOutputs().get(0).isSpent());
//
//        Mockito.verify(transactionEmitter, Mockito.times(1)).send(Mockito.any(TransactionModel.class));
//    }
//
//    @Test
//    void testCreateReward_shouldCreateAndSignRewardTransaction() throws Exception {
//        // Given
//        Address address = WalletTestData.ADDRESS_ALPHA;
//        Coin amount = new Coin(BigDecimal.ONE);
//
//        // When
//        Mockito.when(walletService.sign(Mockito.eq(address), Mockito.any(String.class)))
//                .thenReturn(TransactionTestData.SIGNATURE);
//
//        // Then
//        TransactionModel result = transactionService.createReward(address, amount);
//
//        Assertions.assertNotNull(result);
//        Assertions.assertNotNull(result.getHashId());
//        Assertions.assertEquals(address, result.getSenderAddress());
//        Assertions.assertEquals(address, result.getRecipientAddress());
//        Assertions.assertEquals(amount, result.getAmount());
//        Assertions.assertEquals(new Coin(BigDecimal.ZERO), result.getFee());
//        Assertions.assertEquals(TransactionType.REWARD, result.getType());
//        Assertions.assertNotNull(result.getCreatedAt());
//        Assertions.assertEquals(TransactionTestData.SIGNATURE, result.getSignature());
//
//        Assertions.assertEquals(0, result.getInputs().size());
//
//        Assertions.assertEquals(1, result.getOutputs().size());
//        Assertions.assertEquals(result.getHashId(), result.getOutputs().get(0).getTransactionHashId());
//        Assertions.assertEquals(OutputIndex.RECIPIENT.getIndex(), result.getOutputs().get(0).getOutputIndex());
//        Assertions.assertEquals(address, result.getOutputs().get(0).getRecipientAddress());
//        Assertions.assertEquals(amount, result.getOutputs().get(0).getAmount());
//        Assertions.assertNotNull(result.getOutputs().get(0).getCreatedAt());
//        Assertions.assertFalse(result.getOutputs().get(0).isSpent());
//
//        Mockito.verify(transactionEmitter, Mockito.never()).send(Mockito.any(TransactionModel.class));
//    }
//
//    @Test
//    void testCreateTransfer_shouldThrowInsufficientBalanceException() throws Exception {
//        // Given - transaction requires 1 and UTXO contains .5
//        TransactionModel transaction = TransactionTestData.getTransferPreInitialise();
//
//        UtxoModel utxo = UtxoTestData.getInputUtxoAlpha();
//        utxo.setAmount(new Coin(BigDecimal.valueOf(0.5)));
//
//        // When
//        Mockito.when(feeService.calculateFee()).thenReturn(TransactionTestData.FEE);
//        Mockito.when(utxoRepository.retrieveUnspentUtxosByRecipientAddress(transaction.getSenderAddress().value()))
//                .thenReturn(List.of(utxo));
//
//        // Then
//        Assertions.assertThrows(IllegalStateException.class, () -> transactionService.createTransfer(transaction));
//
//        Mockito.verify(walletService, Mockito.never()).sign(Mockito.any(), Mockito.any());
//        Mockito.verify(utxoRepository, Mockito.never()).updateUnspentUtxoToSpent(Mockito.any());
//    }
//
//    @Test
//    void testCreateTransfer_shouldThrowIllegalStateException() throws Exception {
//        // Given
//        TransactionModel transaction = TransactionTestData.getTransferPreInitialise();
//
//        UtxoModel utxo = UtxoTestData.getInputUtxoAlpha();
//
//        // When
//        Mockito.when(feeService.calculateFee()).thenReturn(TransactionTestData.FEE);
//        Mockito.when(utxoRepository.retrieveUnspentUtxosByRecipientAddress(transaction.getSenderAddress().value()))
//                .thenReturn(List.of(utxo));
//        Mockito.when(walletService.sign(Mockito.eq(transaction.getSenderAddress()), Mockito.any(String.class)))
//                .thenThrow(KeyStoreException.class);
//
//        // Then
//        Assertions.assertThrows(IllegalStateException.class, () -> transactionService.createTransfer(transaction));
//
//        Mockito.verify(transactionEmitter, Mockito.never()).send(Mockito.any(TransactionModel.class));
//    }
//
//    @Test
//    void testCreateTransfer_shouldThrowCryptographicException() throws Exception {
//        // Given
//        TransactionModel transaction = TransactionTestData.getTransferPreInitialise();
//
//        UtxoModel utxo = UtxoTestData.getInputUtxoAlpha();
//
//        // When
//        Mockito.when(feeService.calculateFee()).thenReturn(TransactionTestData.FEE);
//        Mockito.when(utxoRepository.retrieveUnspentUtxosByRecipientAddress(transaction.getSenderAddress().value()))
//                .thenReturn(List.of(utxo));
//        Mockito.when(walletService.sign(Mockito.eq(transaction.getSenderAddress()), Mockito.any(String.class)))
//                .thenThrow(CryptographicException.class);
//
//        // Then
//        Assertions.assertThrows(CryptographicException.class, () -> transactionService.createTransfer(transaction));
//
//        Mockito.verify(transactionEmitter, Mockito.never()).send(Mockito.any(TransactionModel.class));
//    }
//
//    @Test
//    void testCreateReward_shouldThrowIllegalStateException() throws Exception {
//        // Given
//        Address address = WalletTestData.ADDRESS_ALPHA;
//        Coin amount = new Coin(BigDecimal.ONE);
//
//        // When
//        Mockito.when(walletService.sign(Mockito.eq(address), Mockito.any(String.class)))
//                .thenThrow(KeyStoreException.class);
//
//        // Then
//        Assertions.assertThrows(IllegalStateException.class, () -> transactionService.createReward(address, amount));
//
//        Mockito.verify(transactionEmitter, Mockito.never()).send(Mockito.any(TransactionModel.class));
//    }
//
//    @Test
//    void testCreateReward_shouldThrowCryptographicException() throws Exception {
//        // Given
//        Address address = WalletTestData.ADDRESS_ALPHA;
//        Coin amount = new Coin(BigDecimal.ONE);
//
//        // When
//        Mockito.when(walletService.sign(Mockito.eq(address), Mockito.any(String.class)))
//                .thenThrow(CryptographicException.class);
//
//        // Then
//        Assertions.assertThrows(CryptographicException.class, () -> transactionService.createReward(address, amount));
//
//        Mockito.verify(transactionEmitter, Mockito.never()).send(Mockito.any(TransactionModel.class));
//    }
}
