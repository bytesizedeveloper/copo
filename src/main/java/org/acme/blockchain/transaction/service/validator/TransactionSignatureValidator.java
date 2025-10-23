package org.acme.blockchain.transaction.service.validator;

import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.wallet.service.WalletService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class TransactionSignatureValidator implements RewardValidator, TransferValidator {

    private final WalletService walletService;

    @Inject
    public TransactionSignatureValidator(WalletService walletService) {
        this.walletService = walletService;
    }

    @Override
    public void validate(TransactionModel transaction, TransactionValidationModel validationResult) {
        String hashId = transaction.getHashId();
        String signature = transaction.getSignature();

        if (hashId == null) {
            validationResult.addFailure(transaction + "Hash ID is null.");
        }

        if (signature == null) {
            validationResult.addFailure(transaction + "Signature is null.");
        }

        if (hashId != null && signature != null && walletService.verifySignature(transaction.getSenderPublicKeyEncoded(), hashId, signature)) {
            validationResult.addFailure(transaction + "Signature is invalid.");
        }
    }
}
