package org.acme.blockchain.transaction.service.validator;

import org.acme.blockchain.common.utility.HashUtility;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class TransactionHashValidator implements RewardValidator, TransferValidator {

    @Override
    public void validate(TransactionModel transaction, TransactionValidationModel validationResult) {
        String hashId = transaction.getHashId();
        String data = transaction.getData();

        if (hashId == null) {
            validationResult.addFailure(transaction + "Hash ID is null.");
        }

        if (data == null) {
            validationResult.addFailure(transaction + "Data is null.");
        }

        if (hashId != null && data != null && !hashId.equals(HashUtility.calculateSHA256(data))) {
            validationResult.addFailure(transaction + "Hash ID is invalid.");
        }
    }
}
