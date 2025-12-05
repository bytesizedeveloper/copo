package org.acme.blockchain.transaction.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.acme.blockchain.common.model.Coin;
import org.acme.blockchain.common.service.FeeService;
import org.acme.blockchain.common.service.TransferCacheService;
import org.acme.blockchain.common.utility.TimestampUtility;
import org.acme.blockchain.transaction.model.TransactionHash;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransactionSignature;
import org.acme.blockchain.transaction.model.TransferModel;
import org.acme.blockchain.transaction.model.UtxoId;
import org.acme.blockchain.transaction.model.UtxoModel;
import org.acme.blockchain.transaction.model.enumeration.TransactionStatus;
import org.acme.blockchain.transaction.repository.TransactionRepository;
import org.acme.blockchain.transaction.repository.UtxoRepository;
import org.acme.blockchain.wallet.service.WalletService;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jooq.exception.NoDataFoundException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service layer component responsible for managing the lifecycle of {@link TransferModel} transactions.
 * <p>
 * This includes validation, preparing transaction data (inputs/outputs, fees, public keys),
 * calculating the transaction hash, signing, and initiating the asynchronous process of
 * broadcasting the transaction to the network via the reactive messaging channel.
 */
@Slf4j
@ApplicationScoped
public class TransactionService {

    private final TransferCacheService cache;

    private final WalletService walletService;

    private final FeeService feeService;

    private final TransactionRepository transactionRepository;

    private final UtxoRepository utxoRepository;

    private final Emitter<TransferModel> transferEmitter;

    @Inject
    public TransactionService(
            TransferCacheService cache,
            WalletService walletService,
            FeeService feeService,
            TransactionRepository transactionRepository,
            UtxoRepository utxoRepository,
            @Channel("transfer-process") Emitter<TransferModel> transferEmitter
    ) {
        this.cache = cache;
        this.walletService = walletService;
        this.feeService = feeService;
        this.transactionRepository = transactionRepository;
        this.utxoRepository = utxoRepository;
        this.transferEmitter = transferEmitter;
    }

    /**
     * Initiates a new fund transfer transaction.
     * <p>
     * This method orchestrates the transaction creation process: data preparation (pre-hash),
     * cryptographic hashing, finalization (post-hash/signing), and broadcasting the transaction
     * to the network for asynchronous verification and inclusion in a block.
     *
     * @param transfer The initial {@link TransferModel} containing sender, recipient, and amount.
     * @return The finalised {@link TransferModel} object, including its unique hash ID and signature.
     * @throws IllegalStateException If the sender has insufficient UTXOs (funds) to cover the
     * amount and the transaction fee.
     */
    public TransferModel create(TransferModel transfer) {
        preHash(transfer);

        transfer.calculateHashId();
        log.debug("{} Hash ID calculated: {}", transfer, transfer.getHashId());

        postHash(transfer);

        transferEmitter.send(transfer);

        return transfer;
    }

    /**
     * Retrieves a transaction record from the repository using its unique hash ID.
     *
     * @param hashId The unique {@link TransactionHash} identifier of the transaction.
     * @return The retrieved {@link TransferModel} if it exists.
     * @throws NotFoundException If the transaction corresponding to the hash ID is not found in the database.
     */
    public TransactionModel get(TransactionHash hashId) {
        if (cache.containsTransfer(hashId)) {
            return cache.get(hashId);

        } else {
            TransactionModel transaction = getTransaction(hashId);

            List<UtxoModel> outputs = getOutputs(hashId);

            List<UtxoModel> inputs = List.of();
            if (transaction.isTransfer()) {
                inputs = getInputs(transaction);
            }

            transaction.setInputs(inputs);
            transaction.setOutputs(outputs);

            return transaction;
        }
    }

    /**
     * Prepares all required data fields *before* the transaction's hash is calculated.
     * <p>
     * This data, which includes the timestamp, sender public key, fees, and determined UTXO inputs,
     * must be included in the transaction hash to ensure its immutability.
     *
     * @param transfer The {@link TransferModel} to be populated.
     */
    private void preHash(TransferModel transfer) {
        OffsetDateTime now = TimestampUtility.getOffsetDateTimeNow();
        transfer.setCreatedAt(now);

        byte[] senderPublicKeyEncoded = walletService.getPublicKeyEncoded(transfer.getSenderAddress());
        transfer.setSenderPublicKeyEncoded(senderPublicKeyEncoded);

        Coin fee = feeService.calculateFee();
        transfer.setFee(fee);
        log.debug("{} Fee calculated: {}", transfer, transfer.getFee());

        List<UtxoModel> inputs = determineInputs(transfer);
        transfer.setInputs(inputs);
        log.debug("{} Inputs determined: {}", transfer, transfer.getInputs());
    }

    /**
     * Finalises the transaction data *after* the transaction's hash ID has been calculated.
     * <p>
     * This step typically involves elements that depend on the final transaction ID, such as
     * the digital signature, which signs the hash ID.
     *
     * @param transfer The {@link TransferModel} with a calculated hash ID to be finalised.
     */
    private void postHash(TransferModel transfer) {
        TransactionSignature signature = walletService.sign(transfer.getSenderAddress(), transfer.getHashId().value());
        transfer.setSignature(signature);
        log.debug("{} Signed: {}", transfer, transfer.getSignature());

        transfer.generateOutputs();
        log.debug("{} Outputs generated: {}", transfer, transfer.getOutputs());

        transfer.setStatus(TransactionStatus.INITIALISED);
    }

    /**
     * Determines the set of unspent transaction outputs (UTXOs) required to fund the transaction.
     * <p>
     * This method selects UTXOs from the sender's available balance that meet or exceed the
     * total required amount (transfer amount + fee). This process enforces the UTXO model.
     *
     * @param transaction The transaction for which inputs are being determined.
     * @return A {@link List} of {@link UtxoModel} selected as inputs for this transaction.
     * @throws IllegalStateException If the total available UTXO balance is less than the required amount.
     */
    private List<UtxoModel> determineInputs(TransferModel transaction) {
        List<UtxoModel> unspentUtxos = utxoRepository.retrieveUnspentUtxosByRecipientAddress(transaction.getSenderAddress().value());

        Coin totalRequired = transaction.getTotalRequired();

        Coin available = new Coin(BigDecimal.ZERO);
        List<UtxoModel> requiredForInput = new ArrayList<>();

        for (UtxoModel unspentUtxo : unspentUtxos) {
            if (available.isGreaterThanOrEqualTo(totalRequired)) {
                break;
            }

            if (!cache.containsInput(unspentUtxo.getId())) {
                requiredForInput.add(unspentUtxo);
                available = available.add(unspentUtxo.getAmount());
            }
        }

        if (available.isLessThan(totalRequired)) {
            throw new IllegalStateException("Sender has insufficient balance. Required: " + totalRequired
                    + " (Amount: " + transaction.getAmount()
                    + " + Fee: " + transaction.getFee()
                    + ") Available: " + available);
        }

        return requiredForInput;
    }

    private TransactionModel getTransaction(TransactionHash hashId) {
        try {
            return transactionRepository.retrieveTransactionByHashId(hashId.value());
        } catch (NoDataFoundException e) {
            throw new NotFoundException("Transaction does not exist in the cache or database: " + hashId);
        }
    }

    private List<UtxoModel> getInputs(TransactionModel transaction) {
        try {
            List<UtxoId> ids = Arrays.stream(transaction.getInputIds()).map(UtxoId::new).toList();
            return utxoRepository.retrieveUtxosById(ids);
        } catch (NoDataFoundException e) {
            throw new NotFoundException("Transaction inputs do not exist in the cache or database: " + transaction.getHashId());
        }
    }

    private List<UtxoModel> getOutputs(TransactionHash hashId) {
        try {
            return utxoRepository.retrieveUtxoByTransactionHashId(hashId.value());
        } catch (NoDataFoundException e) {
            throw new NotFoundException("Transaction outputs do not exist in the cache or database: " + hashId);
        }
    }
}
