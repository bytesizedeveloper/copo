package org.acme.blockchain.transaction.service.validator.reward;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.blockchain.transaction.model.RewardModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.model.UtxoModel;
import org.acme.blockchain.transaction.service.validator.RewardValidator;

import java.util.List;

@ApplicationScoped
public class RewardOutputsValidator implements RewardValidator {

    @Override
    public void validate(RewardModel reward, TransactionValidationModel validationResult) {
        List<UtxoModel> outputs = reward.getOutputs();

        if (outputs == null) {
            validationResult.addFailure(reward + " Outputs are null.");
        }

        if (outputs != null && outputs.size() != 1) {
            validationResult.addFailure(reward + " Outputs size not equal to 1: " + outputs.size());
        }

        if (outputs != null && outputs.size() == 1) {
            UtxoModel output = outputs.getFirst();

//            if (!reward.getHashId().equals(output.getTransactionHashId())) {
//                validationResult.addFailure(output + " Output transaction hash ID does not equal parent transaction hash ID ("
//                        + reward.getHashId() + "): " + output.getTransactionHashId());
//            }
//
//            if (!OutputIndex.RECIPIENT.getIndex().equals(output.getOutputIndex())) {
//                validationResult.addFailure(output + " Output index does not equal recipient output index ("
//                        + OutputIndex.RECIPIENT.getIndex() + "): " + output.getOutputIndex());
//            }

            if (!reward.getRecipientAddress().equals(output.getRecipientAddress())) {
                validationResult.addFailure(output + " Output recipient does not equal transaction recipient ("
                        + reward.getRecipientAddress() + "): " + output.getRecipientAddress());
            }

            if (!reward.getAmount().equals(output.getAmount())) {
                validationResult.addFailure(output + " Output amount does not equal transaction amount ("
                        + reward.getAmount() + "): " + output.getAmount());
            }

            if (!reward.getCreatedAt().equals(output.getCreatedAt())) {
                validationResult.addFailure(output + " Output timestamp does not equal transaction timestamp ("
                        + reward.getCreatedAt() + "): " + output.getCreatedAt());
            }

            if (output.isSpent()) {
                validationResult.addFailure(output + " Output is already spent.");
            }
        }
    }
}
