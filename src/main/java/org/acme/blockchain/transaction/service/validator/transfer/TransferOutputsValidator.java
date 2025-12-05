package org.acme.blockchain.transaction.service.validator.transfer;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.acme.blockchain.common.model.Coin;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.model.TransferModel;
import org.acme.blockchain.transaction.model.UtxoModel;
import org.acme.blockchain.transaction.service.validator.TransferValidator;

import java.util.List;

@Slf4j
@ApplicationScoped
public class TransferOutputsValidator implements TransferValidator {

    @Override
    public void validate(TransferModel transfer, TransactionValidationModel validationResult) {
        List<UtxoModel> outputs = transfer.getOutputs();

        if (outputs == null) {
            validationResult.addFailure(transfer + " Outputs are null.");
        }

        if (outputs != null && outputs.isEmpty()) {
            validationResult.addFailure(transfer + " Outputs are empty.");
        }

        if (outputs != null && outputs.size() == 1) {
            validateRecipient(transfer, validationResult, outputs.getFirst());
        }

        if (outputs != null && outputs.size() == 2) {
            validateRecipient(transfer, validationResult, outputs.getFirst());
            validateSender(transfer, validationResult, outputs.get(1));
        }

        if (outputs != null && outputs.size() > 2) {
            validationResult.addFailure(transfer + " Outputs size exceeds maximum expected size (2): " + outputs.size());
        }
    }

    private void validateRecipient(
            TransactionModel transaction,
            TransactionValidationModel validationResult,
            UtxoModel output
    ) {
//        if (!transaction.getHashId().equals(output.getTransactionHashId())) {
//            validationResult.addFailure(output + " Output transaction hash ID does not equal parent transaction hash ID ("
//                    + transaction.getHashId() + "): " + output.getTransactionHashId());
//        }

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

    private void validateSender(
            TransferModel transfer,
            TransactionValidationModel validationResult,
            UtxoModel output
    ) {
//        if (!transfer.getHashId().equals(output.getTransactionHashId())) {
//            validationResult.addFailure(output + " Output transaction hash ID does not equal parent transaction hash ID ("
//                    + transfer.getHashId() + "): " + output.getTransactionHashId());
//        }

        if (!transfer.getSenderAddress().equals(output.getRecipientAddress())) {
            validationResult.addFailure(output + " Output recipient does not equal transaction sender ("
                    + transfer.getSenderAddress() + "): " + output.getRecipientAddress());
        }

        Coin totalValueOfInputs = transfer.getTotalValueOfInputs();
        Coin totalRequired = transfer.getTotalRequired();
        Coin change = totalValueOfInputs.subtract(totalRequired);

        if (!change.isEqualTo(output.getAmount())) {
            validationResult.addFailure(output + " Output amount does not equal change ("
                    + change + "): " + output.getAmount());
        }

        if (!transfer.getCreatedAt().equals(output.getCreatedAt())) {
            validationResult.addFailure(output + " Output timestamp does not equal transaction timestamp ("
                    + transfer.getCreatedAt() + "): " + output.getCreatedAt());
        }

        if (output.isSpent()) {
            validationResult.addFailure(output + " Output is already spent.");
        }
    }
}
