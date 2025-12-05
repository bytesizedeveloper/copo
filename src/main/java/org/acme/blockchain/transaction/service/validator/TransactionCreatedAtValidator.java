package org.acme.blockchain.transaction.service.validator;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.blockchain.common.utility.TimestampUtility;
import org.acme.blockchain.transaction.model.RewardModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.model.TransferModel;

@ApplicationScoped
public class TransactionCreatedAtValidator implements RewardValidator, TransferValidator {

    @Override
    public void validate(RewardModel reward, TransactionValidationModel validationResult) {
        if (!TimestampUtility.isWithinMinute(reward.getCreatedAt())) {
            validationResult.addFailure(reward + " Timestamp is older than one minute: " + reward.getCreatedAt());
        }
    }

    @Override
    public void validate(TransferModel transfer, TransactionValidationModel validationResult) {
        if (!TimestampUtility.isWithinMinute(transfer.getCreatedAt())) {
            validationResult.addFailure(transfer + " Timestamp is older than one minute: " + transfer.getCreatedAt());
        }
    }
}
