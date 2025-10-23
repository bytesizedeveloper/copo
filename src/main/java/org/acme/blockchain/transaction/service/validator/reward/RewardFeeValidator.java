package org.acme.blockchain.transaction.service.validator.reward;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.blockchain.common.model.CoinModel;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.service.validator.RewardValidator;

@ApplicationScoped
public class RewardFeeValidator implements RewardValidator {

    @Override
    public void validate(TransactionModel transaction, TransactionValidationModel validationResult) {
        CoinModel fee = transaction.getFee();

        if (!fee.isZero()) {
            validationResult.addFailure(transaction + "Fee is a non-zero value: " + fee);
        }
    }
}
