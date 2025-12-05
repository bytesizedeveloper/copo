package org.acme.blockchain.transaction.service.validator;

import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.model.TransferModel;

public interface TransferValidator {

    void validate(TransferModel transfer, TransactionValidationModel validationResult);
}
