package org.acme.blockchain.transaction.service.validator.reward;

import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.model.UtxoModel;
import org.acme.blockchain.transaction.service.validator.RewardValidator;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class RewardInputsValidator implements RewardValidator {

    @Override
    public void validate(TransactionModel transaction, TransactionValidationModel validationResult) {
        List<UtxoModel> inputs = transaction.getInputs();

        if (inputs == null) {
            validationResult.addFailure(transaction + "Inputs are null.");
        }

        if (inputs != null && !inputs.isEmpty()) {
            validationResult.addFailure(transaction + "Inputs are non-empty: " + inputs);
        }
    }
}
