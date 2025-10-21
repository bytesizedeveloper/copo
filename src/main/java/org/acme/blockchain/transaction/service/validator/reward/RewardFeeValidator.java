package org.acme.blockchain.transaction.service.validator.reward;

import org.acme.blockchain.transaction.model.CoinModel;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.service.validator.RewardValidator;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RewardFeeValidator implements RewardValidator {

    @Override
    public void validate(TransactionModel transaction, TransactionValidationModel validationResult) {
        CoinModel fee = transaction.getFee();

        if (fee == null) {
            validationResult.addFailure(transaction + "Fee is null.");
        }

        if (fee != null && !fee.isZero()) {
            validationResult.addFailure(transaction + "Fee is a non-zero value: " + fee);
        }
    }
}
