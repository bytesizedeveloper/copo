package org.acme.blockchain.transaction.service.validator.reward;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.blockchain.transaction.model.RewardModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.service.validator.RewardValidator;

@ApplicationScoped
public class RewardAddressValidator implements RewardValidator {

    @Override
    public void validate(RewardModel reward, TransactionValidationModel validationResult) {
        if (!reward.getSenderAddress().equals(reward.getRecipientAddress())) {
            validationResult.addFailure(reward + " Sender and recipient address are not equal: " + reward.getSenderAddress());
        }
    }
}
