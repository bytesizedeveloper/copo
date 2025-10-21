package org.acme.blockchain.transaction.service.validator;

import org.acme.blockchain.common.utility.TimestampUtility;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

@Slf4j
@ApplicationScoped
public class TransactionCreatedAtValidator implements RewardValidator, TransferValidator {

    @Override
    public void validate(TransactionModel transaction, TransactionValidationModel validationResult) {
        OffsetDateTime createdAt = transaction.getCreatedAt();

        if (createdAt == null) {
            validationResult.addFailure(transaction + "Timestamp is null.");
        }

        if (!TimestampUtility.isWithinMinute(createdAt)) {
            validationResult.addFailure(transaction + "Timestamp is older than a minute: " + createdAt);
        }
    }
}
