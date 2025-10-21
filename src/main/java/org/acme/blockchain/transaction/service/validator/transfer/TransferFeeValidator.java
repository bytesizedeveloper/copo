package org.acme.blockchain.transaction.service.validator.transfer;

import org.acme.blockchain.transaction.model.CoinModel;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.service.validator.TransferValidator;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransferFeeValidator implements TransferValidator {

    @Override
    public void validate(TransactionModel transaction, TransactionValidationModel validationResult) {
        CoinModel fee = transaction.getFee();

        if (fee == null) {
            validationResult.addFailure(transaction + "Fee is null.");
        }

        if (fee != null && fee.isLessThanMinimum()) {
            validationResult.addFailure(transaction + "Fee does not exceed or is equal to the minimum value (" + CoinModel.MINIMUM + "): " + fee);
        }

        if (fee != null && fee.isGreaterThanMaximum()) {
            validationResult.addFailure(transaction + "Fee exceeds or is equal to the maximum value (" + CoinModel.MAXIMUM + "): " + fee);
        }
    }
}
