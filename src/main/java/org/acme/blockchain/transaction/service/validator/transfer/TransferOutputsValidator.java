package org.acme.blockchain.transaction.service.validator.transfer;

import org.acme.blockchain.transaction.model.CoinModel;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.model.UtxoModel;
import org.acme.blockchain.transaction.service.validator.TransferValidator;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
public class TransferOutputsValidator implements TransferValidator {

    @Override
    public void validate(TransactionModel transaction, TransactionValidationModel validationResult) {
        List<UtxoModel> outputs = transaction.getOutputs();

        if (outputs == null) {
            validationResult.addFailure(transaction + "Outputs are null.");
        }

        if (outputs != null && outputs.isEmpty()) {
            validationResult.addFailure(transaction + "Outputs are empty.");
        }

        if (outputs != null && outputs.size() == 1) {
            validateRecipient(transaction, validationResult, outputs.getFirst());
        }

        if (outputs != null && outputs.size() == 2) {
            validateRecipient(transaction, validationResult, outputs.getFirst());
            validateSender(transaction, validationResult, outputs.get(1));
        }

        if (outputs != null && outputs.size() > 2) {
            validationResult.addFailure(transaction + "Outputs size exceeds maximum expected size (2): " + outputs.size());
        }
    }

    private void validateRecipient(
            TransactionModel transaction,
            TransactionValidationModel validationResult,
            UtxoModel output
    ) {
        if (!transaction.getHashId().equals(output.getTransactionHashId())) {
            validationResult.addFailure(output + " Output transaction hash ID does not equal parent transaction hash ID ("
                    + transaction.getHashId() + "): " + output.getTransactionHashId());
        }

        if (!UtxoModel.OUTPUT_INDEX_RECIPIENT.equals(output.getOutputIndex())) {
            validationResult.addFailure(output + " Output index does not equal recipient output index ("
                    + UtxoModel.OUTPUT_INDEX_RECIPIENT + "): " + output.getOutputIndex());
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

    private void validateSender(
            TransactionModel transaction,
            TransactionValidationModel validationResult,
            UtxoModel output
    ) {
        if (!transaction.getHashId().equals(output.getTransactionHashId())) {
            validationResult.addFailure(output + " Output transaction hash ID does not equal parent transaction hash ID ("
                    + transaction.getHashId() + "): " + output.getTransactionHashId());
        }

        if (!UtxoModel.OUTPUT_INDEX_SENDER.equals(output.getOutputIndex())) {
            validationResult.addFailure(output + " Output index does not equal sender output index ("
                    + UtxoModel.OUTPUT_INDEX_SENDER + "): " + output.getOutputIndex());
        }

        if (!transaction.getSenderAddress().equals(output.getRecipientAddress())) {
            validationResult.addFailure(output + " Output recipient does not equal transaction sender ("
                    + transaction.getSenderAddress() + "): " + output.getRecipientAddress());
        }

        CoinModel totalValueOfInputs = transaction.getTotalValueOfInputs();
        CoinModel totalRequired = transaction.getTotalRequired();
        CoinModel change = totalValueOfInputs.subtract(totalRequired);

        if (!change.isEqualTo(output.getAmount())) {
            validationResult.addFailure(output + " Output amount does not equal change ("
                    + change + "): " + output.getAmount());
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
