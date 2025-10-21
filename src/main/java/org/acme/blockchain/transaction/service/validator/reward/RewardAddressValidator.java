package org.acme.blockchain.transaction.service.validator.reward;

import org.acme.blockchain.common.utility.WalletUtility;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.service.validator.RewardValidator;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RewardAddressValidator implements RewardValidator {

    @Override
    public void validate(TransactionModel transaction, TransactionValidationModel validationResult) {
        String sender = transaction.getSenderAddress();
        String recipient = transaction.getRecipientAddress();

        if (!WalletUtility.isValid(sender)) {
            validationResult.addFailure(transaction + "Sender address is invalid format: " + sender);
        }

        if (!WalletUtility.isValid(recipient)) {
            validationResult.addFailure(transaction + "Recipient address is invalid format: " + recipient);
        }

        if (!sender.equals(recipient)) {
            validationResult.addFailure(transaction + "Sender and recipient address are not equal: " + sender);
        }
    }
}
