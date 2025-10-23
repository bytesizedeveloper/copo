package org.acme.blockchain.transaction.service.validator.reward;

import org.acme.blockchain.common.model.AddressModel;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.service.validator.RewardValidator;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RewardAddressValidator implements RewardValidator {

    @Override
    public void validate(TransactionModel transaction, TransactionValidationModel validationResult) {
        AddressModel sender = transaction.getSenderAddress();
        AddressModel recipient = transaction.getRecipientAddress();

        if (!sender.equals(recipient)) {
            validationResult.addFailure(transaction + "Sender and recipient address are not equal: " + sender);
        }
    }
}
