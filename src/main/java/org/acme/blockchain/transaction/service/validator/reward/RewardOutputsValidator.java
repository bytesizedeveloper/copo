package org.acme.blockchain.transaction.service.validator.reward;

import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.model.UtxoModel;
import org.acme.blockchain.transaction.model.enumeration.OutputIndex;
import org.acme.blockchain.transaction.service.validator.RewardValidator;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class RewardOutputsValidator implements RewardValidator {

    @Override
    public void validate(TransactionModel transaction, TransactionValidationModel validationResult) {
        List<UtxoModel> outputs = transaction.getOutputs();

        if (outputs == null) {
            validationResult.addFailure(transaction + "Outputs are null.");
        }

        if (outputs != null && outputs.size() != 1) {
            validationResult.addFailure(transaction + "Outputs size not equal to 1: " + outputs.size());
        }

        if (outputs != null && outputs.size() == 1) {
            UtxoModel output = outputs.getFirst();

            if (!transaction.getHashId().equals(output.getTransactionHashId())) {
                validationResult.addFailure(output + " Output transaction hash ID does not equal parent transaction hash ID ("
                        + transaction.getHashId() + "): " + output.getTransactionHashId());
            }

            if (!OutputIndex.RECIPIENT.getIndex().equals(output.getOutputIndex())) {
                validationResult.addFailure(output + " Output index does not equal recipient output index ("
                        + OutputIndex.RECIPIENT.getIndex() + "): " + output.getOutputIndex());
            }

            if (!transaction.getRecipientAddress().equals(output.getRecipientAddress())) {
                validationResult.addFailure(output + " Output recipient does not equal transaction recipient ("
                        + transaction.getRecipientAddress() + "): " + output.getRecipientAddress());
            }

            if (!transaction.getAmount().equals(output.getAmount())) {
                validationResult.addFailure(output + " Output amount does not equal transaction amount ("
                        + transaction.getAmount() + "): " + output.getAmount());
            }

            if (!transaction.getCreatedAt().equals(output.getCreatedAt())) {
                validationResult.addFailure(output + " Output timestamp does not equal transaction timestamp ("
                        + transaction.getCreatedAt() + "): " + output.getCreatedAt());
            }

            if (output.isSpent()) {
                validationResult.addFailure(output + " Output is already spent.");
            }
        }
    }
}
