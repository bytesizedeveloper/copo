package org.acme.blockchain.transaction.service.validator;

import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;

public interface TransferValidator {

    void validate(TransactionModel transaction, TransactionValidationModel validationResult);
}
