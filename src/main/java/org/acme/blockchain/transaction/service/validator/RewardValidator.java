package org.acme.blockchain.transaction.service.validator;

import org.acme.blockchain.transaction.model.RewardModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;

public interface RewardValidator {

    void validate(RewardModel reward, TransactionValidationModel validationResult);
}
