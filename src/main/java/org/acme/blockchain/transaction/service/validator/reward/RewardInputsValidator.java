package org.acme.blockchain.transaction.service.validator.reward;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.blockchain.transaction.model.RewardModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.service.validator.RewardValidator;

@ApplicationScoped
public class RewardInputsValidator implements RewardValidator {

    @Override
    public void validate(RewardModel reward, TransactionValidationModel validationResult) {
        if (reward.getInputs() != null && !reward.getInputs().isEmpty()) {
            validationResult.addFailure(reward + " Inputs are non-empty: " + reward.getInputs());
        }
    }
}
