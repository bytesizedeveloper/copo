package org.acme.blockchain.transaction.service.validator;

import org.acme.blockchain.transaction.model.CoinModel;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class TransactionAmountValidator implements RewardValidator, TransferValidator {

    @Override
    public void validate(TransactionModel transaction, TransactionValidationModel validationResult) {
        CoinModel amount = transaction.getAmount();

        if (amount == null) {
            validationResult.addFailure(transaction + "Amount is null.");
        }

        if (amount != null && amount.isLessThanMinimum()) {
            validationResult.addFailure(transaction + "Amount does not exceed or is equal to the minimum value (" + CoinModel.MINIMUM + "): " + amount);
        }

        if (amount != null && amount.isGreaterThanMaximum()) {
            validationResult.addFailure(transaction + "Amount exceeds or is equal to the maximum value (" + CoinModel.MAXIMUM + "): " + amount);
        }
    }
}
