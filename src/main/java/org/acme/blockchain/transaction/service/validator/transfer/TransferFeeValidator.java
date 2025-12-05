package org.acme.blockchain.transaction.service.validator.transfer;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.model.TransferModel;
import org.acme.blockchain.transaction.service.validator.TransferValidator;

@ApplicationScoped
public class TransferFeeValidator implements TransferValidator {

    @Override
    public void validate(TransferModel transfer, TransactionValidationModel validationResult) {

    }
}
