package org.acme.blockchain.transaction.service;

import org.acme.blockchain.common.service.TransactionCacheService;
import org.acme.blockchain.transaction.model.TransactionGossip;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.model.enumeration.TransactionStatus;
import org.acme.blockchain.transaction.service.validator.RewardValidator;
import org.acme.blockchain.transaction.service.validator.TransferValidator;
import io.quarkus.arc.All;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.util.List;

/**
 * {@code TransactionCoordinatorService} is responsible for receiving, routing, and processing
 * {@link TransactionModel} objects within the blockchain node.
 * <p>
 * It acts as the main entry point for transaction handling, distinguishing between new transactions (local or peer-originated)
 * and existing ones, coordinating the validation, gossip, confirmation, and rejection processes.
 * It leverages Quarkus's reactive messaging ({@code @Channel}) and virtual threads for non-blocking asynchronous processing.
 */
@Slf4j
@ApplicationScoped
public class TransactionCoordinatorService {

    private final TransactionCacheService cache;

    private final List<RewardValidator> rewardValidators;

    private final List<TransferValidator> transferValidators;

    /**
     * Constructs the TransactionCoordinatorService, injecting required dependencies.
     * <p>
     * Quarkus's {@code @All} annotation is used to inject all implementations of the validator interfaces,
     * allowing for dynamic and extensible validation logic.
     *
     * @param cache The service responsible for storing and retrieving transaction and gossip state.
     * @param rewardValidators A list of all beans implementing {@link RewardValidator} for validating reward transactions.
     * @param transferValidators A list of all beans implementing {@link TransferValidator} for validating transfer transactions.
     */
    @Inject
    public TransactionCoordinatorService(
            TransactionCacheService cache,
            @All List<RewardValidator> rewardValidators,
            @All List<TransferValidator> transferValidators
    ) {
        this.cache = cache;
        this.rewardValidators = rewardValidators;
        this.transferValidators = transferValidators;
    }

    /**
     * Entry point for processing a new transaction via a Quarkus reactive messaging channel.
     * <p>
     * This method immediately offloads the processing work to a lightweight virtual thread
     * to ensure the reactive messaging thread (typically an I/O thread) is not blocked.
     *
     * @param transaction The {@link TransactionModel} received from the reactive channel.
     */
    @Incoming("transaction-process")
    public void process(TransactionModel transaction) {
        Thread.ofVirtual().start(() -> processThread(transaction));
    }

    /**
     * The core asynchronous processing logic executed on a virtual thread.
     * <p>
     * It checks if the transaction is already known (exists in the cache) and delegates
     * to the appropriate handler ({@link #processExisting(TransactionModel)} or {@link #processNew(TransactionModel)}).
     *
     * @param transaction The {@link TransactionModel} to process.
     */
    private void processThread(TransactionModel transaction) {
        try {
            if (cache.containsTransaction(transaction.getHashId())) {
                processExisting(transaction);
            } else {
                processNew(transaction);
            }
        } catch (IllegalStateException e) {
            log.debug("{} {} {}.", transaction, e.getMessage(), transaction.getStatus());

        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            log.error("{} Transaction processing has failed. Marked as {}.", transaction, transaction.getStatus(), e);
        }
    }

    /**
     * Handles processing for transactions that already exist in the cache.
     * <p>
     * This is typically for updates coming from peers (e.g., a confirmation or rejection).
     *
     * @param transaction The existing {@link TransactionModel} update.
     * @throws IllegalStateException if the existing transaction is in an invalid state for further processing.
     */
    private void processExisting(TransactionModel transaction) {
        if (isActive(transaction)) {
            switch (transaction.getStatus()) {
                case CONFIRMED -> confirmed(transaction);
                case REJECTED -> rejected(transaction);
                default -> throw new IllegalStateException("Existing transaction in invalid state: " + transaction.getStatus());
            }
        }
    }

    /**
     * Handles processing for new transactions (not yet in the cache).
     * <p>
     * It adds the transaction to the cache and routes it based on its initial status.
     *
     * @param transaction The new {@link TransactionModel}.
     * @throws IllegalStateException if the new transaction is in an invalid initial state.
     */
    private void processNew(TransactionModel transaction) {
        cache.addTransaction(transaction.getHashId(), transaction);
        switch (transaction.getStatus()) {
            case INITIALISED -> verifyFromLocal(transaction); // Newly created by this node
            case BROADCASTED, CONFIRMED, REJECTED -> verifyFromPeer(transaction); // Received from a peer
            default -> throw new IllegalStateException("New transaction in invalid state: " + transaction.getStatus());
        }
    }

    /**
     * Validates a transaction that originated from this local node.
     * <p>
     * If valid, it is marked as validated and then immediately broadcast to peers.
     * If invalid, processing stops.
     *
     * @param transaction The locally originated {@link TransactionModel}.
     */
    private void verifyFromLocal(TransactionModel transaction) {
        if (isValid(transaction)) {
            transaction.setStatus(TransactionStatus.VALIDATED);
            log.debug("{} Transaction from local is valid and will be broadcast to peers. Marked as {}.", transaction, transaction.getStatus());

            transaction.setStatus(TransactionStatus.BROADCASTED);
            broadcast(transaction);
        } else {
            transaction.setStatus(TransactionStatus.INVALIDATED);
            log.error("{} Transaction from local is invalid and will not be processed further. Marked as {}.", transaction, transaction.getStatus());
        }
    }

    /**
     * Validates a transaction that originated from a peer node.
     * <p>
     * If valid, it is marked as validated and proceeds to the confirmation step.
     * If invalid, it is immediately marked as rejected.
     *
     * @param transaction The peer-originated {@link TransactionModel}.
     */
    private void verifyFromPeer(TransactionModel transaction) {
        if (isValid(transaction)) {
            transaction.setStatus(TransactionStatus.VALIDATED);
            log.debug("{} Transaction from peer is valid. Marked as {}.", transaction, transaction.getStatus());
            confirmed(transaction);
        } else {
            transaction.setStatus(TransactionStatus.INVALIDATED);
            log.debug("{} Transaction from peer is invalid. Marked as {}.", transaction, transaction.getStatus());
            rejected(transaction);
        }
    }

    /**
     * Processes a confirmation update for a transaction.
     * <p>
     * It increments the confirmation count in the gossip state. If the count meets the network
     * threshold, the transaction is marked {@code CONFIRMED} and made ready for mining.
     * The new gossip state is broadcast to the network.
     *
     * @param transaction The {@link TransactionModel} confirming a state.
     */
    private void confirmed(TransactionModel transaction) {
        TransactionGossip gossip = cache.getOrDefault(transaction.getHashId());
        int confirmations = gossip.confirm();
        log.debug("{} Transaction has received a confirmation. Confirmations: {} Rejections: {}", transaction, confirmations, gossip.getRejections());

        if (confirmations >= determineThreshold()) {
            transaction.setStatus(TransactionStatus.CONFIRMED);
            log.info("{} Transaction has been confirmed by peers and is ready to mine. Marked as {}.", transaction, transaction.getStatus());
            cache.readyToMine(transaction);
        }

        cache.addGossip(transaction.getHashId(), gossip);
        broadcast(transaction, TransactionStatus.CONFIRMED);

        // Ensures the transaction remains in a broadcastable state if it was only VALIDATED locally
        if (TransactionStatus.VALIDATED.equals(transaction.getStatus())) {
            transaction.setStatus(TransactionStatus.BROADCASTED);
        }
    }

    /**
     * Processes a rejection update for a transaction.
     * <p>
     * It increments the rejection count in the gossip state. If the count exceeds the network's
     * rejection threshold, the transaction is marked {@code REJECTED} and stops processing.
     * The new gossip state is broadcast to the network.
     *
     * @param transaction The {@link TransactionModel} rejecting a state.
     */
    private void rejected(TransactionModel transaction) {
        TransactionGossip gossip = cache.getOrDefault(transaction.getHashId());
        int rejections = gossip.reject();
        log.debug("{} Transaction has received a rejection. Confirmations: {} Rejections: {}", transaction, gossip.getConfirmations(), rejections);

        if (rejections <= determineThreshold()) {
            transaction.setStatus(TransactionStatus.REJECTED);
            log.error("{} Transaction has been rejected by peers and will not be processed further. Marked as {}.", transaction, transaction.getStatus());
        }

        cache.addGossip(transaction.getHashId(), gossip);
        broadcast(transaction, TransactionStatus.REJECTED);

        // Ensures the transaction remains in a broadcastable state if it was only INVALIDATED locally
        if (TransactionStatus.INVALIDATED.equals(transaction.getStatus())) {
            transaction.setStatus(TransactionStatus.BROADCASTED);
        }
    }

    /**
     * Determines if a transaction is structurally and cryptographically valid based on its type.
     * <p>
     * It delegates the validation to the specific validator lists.
     *
     * @param transaction The {@link TransactionModel} to validate.
     * @return {@code true} if the transaction is valid; {@code false} otherwise.
     */
    private boolean isValid(TransactionModel transaction) {
        if (transaction.getType() == null) {
            return false;
        }

        TransactionValidationModel validationResult = new TransactionValidationModel();

        if (transaction.isReward()) {
            validateReward(transaction, validationResult);
        } else {
            validateTransfer(transaction, validationResult);
        }

        if (validationResult.isSuccessful()) {
            return true;
        } else {
            log.debug("{} Transaction of type {} invalid due to:\n{}", transaction, transaction.getType(), validationResult);
            return false;
        }
    }

    /**
     * Validates a reward transaction using all available {@link RewardValidator}s.
     * <p>
     * Ensures that the transaction is considered valid only if
     * <strong>all</strong> injected {@link RewardValidator} instances return {@code true}.
     * The use of {@code allMatch(validator -> validator.validate(transaction))} guarantees
     * a strict, multi-layered validation process.
     *
     * @param transaction The reward transaction to validate.
     * @return {@code true} if the reward transaction passes all validations; {@code false} otherwise.
     */
    private void validateReward(TransactionModel transaction, TransactionValidationModel validationResult) {
        rewardValidators.forEach(validator -> validator.validate(transaction, validationResult));
    }

    /**
     * Validates a standard transfer transaction using all available {@link TransferValidator}s.
     * <p>
     * Ensures that the transaction is considered valid only if
     * <strong>all</strong> injected {@link TransferValidator} instances return {@code true}.
     * This is crucial for maintaining ledger integrity (e.g., checking for sufficient funds,
     * valid signature, and non-double-spending).
     *
     * @param transaction The transfer transaction to validate.
     * @return {@code true} if the transfer transaction passes all validations; {@code false} otherwise.
     */
    private void validateTransfer(TransactionModel transaction, TransactionValidationModel validationResult) {
        transferValidators.forEach(validator -> validator.validate(transaction, validationResult));
    }

    /**
     * Creates a new {@link TransactionModel} with an updated status and broadcasts it to peers.
     *
     * @param transaction The original transaction.
     * @param status The new {@link TransactionStatus} to set for the broadcast message.
     */
    private void broadcast(TransactionModel transaction, TransactionStatus status) {
        TransactionModel toBroadcast = transaction.toBuilder()
                .status(status)
                .build();
        broadcast(toBroadcast);
    }

    /**
     * Placeholder method for the actual peer-to-peer broadcast mechanism.
     *
     * @param transaction The {@link TransactionModel} to broadcast.
     */
    private void broadcast(TransactionModel transaction) {
        // TODO: Implement actual network broadcast logic
        log.debug("{} Transaction has been broadcast to peers as {}.", transaction, transaction.getStatus());
    }

    /**
     * Determines the network consensus threshold required for a transaction to be confirmed or rejected.
     * <p>
     *
     * @return The required number of confirmations to deem a transaction ready for mining.
     */
    private int determineThreshold() {
        return 1; // Placeholder
    }

    /**
     * Checks if the transaction is in a non-terminal state, meaning it can still be processed.
     *
     * @param transaction The {@link TransactionModel} to check.
     * @return {@code true} if the transaction is not yet {@code CONFIRMED} or {@code REJECTED}; {@code false} otherwise.
     */
    private boolean isActive(TransactionModel transaction) {
        if (transaction.isTerminal()) {
            log.debug("{} Transaction in terminal state: {}", transaction, transaction.getStatus());
            return false;
        } else {
            log.debug("{} Transaction actively processing: {}", transaction, transaction.getStatus());
            return true;
        }
    }
}
