package org.acme.blockchain.transaction.service.validator;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.blockchain.common.utility.HashUtility;
import org.acme.blockchain.transaction.model.RewardModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.model.TransferModel;

@ApplicationScoped
public class TransactionHashValidator implements RewardValidator, TransferValidator {

    @Override
    public void validate(RewardModel reward, TransactionValidationModel validationResult) {
        if (reward.getHashId().value().equals(HashUtility.calculateSHA256(reward.getData()))) {
            validationResult.addFailure(reward + " Hash ID is invalid.");
        }
    }

    @Override
    public void validate(TransferModel transfer, TransactionValidationModel validationResult) {
        if (transfer.getHashId().value().equals(HashUtility.calculateSHA256(transfer.getData()))) {
            validationResult.addFailure(transfer + " Hash ID is invalid.");
        }
    }
}
