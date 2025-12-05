package org.acme.blockchain.transaction.service.validator.transfer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.model.TransferModel;
import org.acme.blockchain.transaction.service.validator.TransferValidator;
import org.acme.blockchain.wallet.service.WalletService;

@ApplicationScoped
public class TransferSignatureValidator implements TransferValidator {

    private final WalletService walletService;

    @Inject
    public TransferSignatureValidator(WalletService walletService) {
        this.walletService = walletService;
    }

    @Override
    public void validate(TransferModel transfer, TransactionValidationModel validationResult) {
        if (!walletService.verifySignature(transfer.getSenderPublicKeyEncoded(), transfer.getHashId().value(), transfer.getSignature())) {
            validationResult.addFailure(transfer + " Signature is invalid.");
        }
    }
}
