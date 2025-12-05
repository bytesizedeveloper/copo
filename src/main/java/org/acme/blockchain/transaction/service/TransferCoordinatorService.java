package org.acme.blockchain.transaction.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.acme.blockchain.common.service.TransferCacheService;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.model.TransferGossip;
import org.acme.blockchain.transaction.model.TransferModel;
import org.acme.blockchain.transaction.model.enumeration.TransactionStatus;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@Slf4j
@ApplicationScoped
public class TransferCoordinatorService {

    private final TransferCacheService cache;

    private final TransactionValidatorService validatorService;

    @Inject
    public TransferCoordinatorService(
            TransferCacheService cache,
            TransactionValidatorService validatorService
    ) {
        this.cache = cache;
        this.validatorService = validatorService;
    }

    @Incoming("transfer-process")
    public void process(TransferModel transfer) {
        Thread.ofVirtual().start(() -> processThread(transfer));
    }

    private void processThread(TransferModel transfer) {
        try {
            if (cache.containsTransfer(transfer.getHashId())) {
                processExisting(transfer);
            } else {
                processNew(transfer);
            }
        } catch (IllegalStateException e) {
            log.debug("{} {} {}.", transfer, e.getMessage(), transfer.getStatus());

        } catch (Exception e) {
            transfer.setStatus(TransactionStatus.FAILED);
            log.error("{} Processing has failed. Marked as {}.", transfer, transfer.getStatus(), e);
        }
    }
    
    private void processExisting(TransferModel transfer) {
        if (isActive(transfer)) {
            switch (transfer.getStatus()) {
                case CONFIRMED -> confirmed(transfer);
                case REJECTED -> rejected(transfer);
                default -> throw new IllegalStateException("Existing transfer in invalid state: " + transfer.getStatus());
            }
        }
    }

    private void processNew(TransferModel transfer) {
        cache.addTransfer(transfer.getHashId(), transfer);
        switch (transfer.getStatus()) {
            case INITIALISED -> verifyFromLocal(transfer);
            case BROADCASTED, CONFIRMED, REJECTED -> verifyFromPeer(transfer);
            default -> throw new IllegalStateException("New transfer in invalid state: " + transfer.getStatus());
        }
    }

    private void verifyFromLocal(TransferModel transfer) {
        if (isValid(transfer)) {
            transfer.getInputs().forEach(input -> cache.addInput(input));

            transfer.setStatus(TransactionStatus.VALIDATED);
            log.debug("{} From local is valid and will be broadcast to peers.", transfer);

            transfer.setStatus(TransactionStatus.BROADCASTED);
            broadcast(transfer);
        } else {
            transfer.setStatus(TransactionStatus.INVALIDATED);
            log.error("{} From local is invalid and will not be processed further.", transfer);
        }
    }

    private void verifyFromPeer(TransferModel transfer) {
        if (isValid(transfer)) {
            transfer.setStatus(TransactionStatus.VALIDATED);
            log.debug("{} From peer is valid.", transfer);
            confirmed(transfer);
        } else {
            transfer.setStatus(TransactionStatus.INVALIDATED);
            log.debug("{} From peer is invalid.", transfer);
            rejected(transfer);
        }
    }

    private void confirmed(TransferModel transfer) {
        TransferGossip gossip = cache.getOrDefault(transfer.getHashId());
        int confirmations = gossip.confirm();
        log.debug("{} Received a confirmation. Confirmations: {} Rejections: {}", transfer, confirmations, gossip.getRejections());

        if (confirmations >= determineThreshold()) {
            transfer.setStatus(TransactionStatus.CONFIRMED);
            log.info("{} Confirmed by peers and is ready to mine.", transfer);
            cache.readyToMine(transfer);
        }

        cache.addGossip(transfer.getHashId(), gossip);
        broadcast(transfer, TransactionStatus.CONFIRMED);

        if (TransactionStatus.VALIDATED.equals(transfer.getStatus())) {
            transfer.setStatus(TransactionStatus.BROADCASTED);
        }
    }

    private void rejected(TransferModel transfer) {
        TransferGossip gossip = cache.getOrDefault(transfer.getHashId());
        int rejections = gossip.reject();
        log.debug("{} Received a rejection. Confirmations: {} Rejections: {}", transfer, gossip.getConfirmations(), rejections);

        if (rejections <= determineThreshold()) {
            transfer.setStatus(TransactionStatus.REJECTED);
            log.error("{} Rejected by peers and will not be processed further.", transfer);
        }

        cache.addGossip(transfer.getHashId(), gossip);
        broadcast(transfer, TransactionStatus.REJECTED);

        if (TransactionStatus.INVALIDATED.equals(transfer.getStatus())) {
            transfer.setStatus(TransactionStatus.BROADCASTED);
        }
    }

    private boolean isValid(TransferModel transfer) {
        TransactionValidationModel validationResult = new TransactionValidationModel();

        validatorService.validateTransfer(transfer, validationResult);

        if (validationResult.isSuccessful()) {
            return true;
        } else {
            log.debug("{} Invalid due to:\n{}", transfer, validationResult);
            return false;
        }
    }

    private void broadcast(TransferModel transfer, TransactionStatus status) {
        TransferModel toBroadcast = transfer.toBuilder()
                .status(status)
                .build();
        broadcast(toBroadcast);
    }

    private void broadcast(TransferModel transfer) {
        log.debug("{} Broadcast to peers as {}.", transfer, transfer.getStatus());
    }

    private int determineThreshold() {
        return 1;
    }

    private boolean isActive(TransferModel transfer) {
        if (transfer.isTerminal()) {
            log.debug("{} In terminal state. Input from peer will be ignored.", transfer);
            return false;
        } else {
            log.debug("{} Actively processing Processing input from peer.", transfer);
            return true;
        }
    }
}
