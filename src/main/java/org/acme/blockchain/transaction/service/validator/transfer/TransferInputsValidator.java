package org.acme.blockchain.transaction.service.validator.transfer;

import org.acme.blockchain.common.service.TransactionCacheService;
import org.acme.blockchain.transaction.model.CoinModel;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.model.UtxoModel;
import org.acme.blockchain.transaction.repository.UtxoRepository;
import org.acme.blockchain.transaction.service.validator.TransferValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class TransferInputsValidator implements TransferValidator {

    private final TransactionCacheService cache;

    private final UtxoRepository utxoRepository;

    @Inject
    public TransferInputsValidator(TransactionCacheService cache, UtxoRepository utxoRepository) {
        this.cache = cache;
        this.utxoRepository = utxoRepository;
    }

    @Override
    public void validate(TransactionModel transaction, TransactionValidationModel validationResult) {
        List<UtxoModel> inputs = transaction.getInputs();

        if (inputs == null) {
            validationResult.addFailure(transaction + "Inputs are null.");
        }

        if (inputs != null && inputs.isEmpty()) {
            validationResult.addFailure(transaction + "Inputs are empty.");
        }

        if (inputs != null && !inputs.isEmpty()) {

            boolean isAfforded = true;

            CoinModel totalValueOfInputs = transaction.getTotalValueOfInputs();
            CoinModel totalRequired = transaction.getTotalRequired();

            if (totalValueOfInputs.isLessThan(totalRequired)) {
                validationResult.addFailure("Inputs cannot afford transaction ("
                        + totalRequired + "): " + totalValueOfInputs);
                isAfforded = false;
            }

            if (isAfforded) {

                for (UtxoModel input : inputs) {

                    if (input.getTransactionHashId() != null && cache.containsInput(input.getTransactionHashId())) {
                        validationResult.addFailure(input + " Input double spent pending mining.");
                        break;
                    }

                    if (utxoRepository.isSpent(input)) {
                        validationResult.addFailure(input + " Input double spent.");
                        break;
                    }

                    if (!(UtxoModel.OUTPUT_INDEX_RECIPIENT.equals(input.getOutputIndex())
                            || UtxoModel.OUTPUT_INDEX_SENDER.equals(input.getOutputIndex()))) {
                        validationResult.addFailure(input + " Input output index invalid format (00 or 01): " + input.getOutputIndex());
                    }

                    if (!transaction.getSenderAddress().equals(input.getRecipientAddress())) {
                        validationResult.addFailure(input + " Input recipient does not equal transaction sender ("
                                + transaction.getSenderAddress() + "): " + input.getRecipientAddress());
                    }

                    if (input.getAmount().isLessThanMinimum()) {
                        validationResult.addFailure(input + " Input amount does not exceed or is equal to the minimum value ("
                                + CoinModel.MINIMUM + "): " + input.getAmount());
                    }

                    if (input.getAmount().isGreaterThanMaximum()) {
                        validationResult.addFailure(input + " Input amount exceeds or is equal to the maximum value ("
                                + CoinModel.MAXIMUM + "): " + input.getAmount());
                    }

                    if (transaction.getCreatedAt().isBefore(input.getCreatedAt())) {
                        validationResult.addFailure(input + " Input timestamp does not precede transaction timestamp ("
                                + transaction.getCreatedAt() + "): " + input.getCreatedAt());
                    }

                    if (input.isSpent()) {
                        validationResult.addFailure(input + " Input is already spent.");
                    }
                }
            }
        }
    }
}
