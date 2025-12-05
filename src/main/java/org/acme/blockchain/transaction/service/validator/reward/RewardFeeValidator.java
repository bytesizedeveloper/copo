package org.acme.blockchain.transaction.service.validator.reward;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.blockchain.transaction.model.RewardModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.service.validator.RewardValidator;

@ApplicationScoped
public class RewardFeeValidator implements RewardValidator {

    @Override
    public void validate(RewardModel reward, TransactionValidationModel validationResult) {
        if (!reward.getFee().isZero()) {
            validationResult.addFailure(reward + " Fee is a non-zero value: " + reward.getFee());
        }
    }
}
