package org.acme.blockchain.transaction.service.validator.transfer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.blockchain.common.model.Coin;
import org.acme.blockchain.common.service.TransferCacheService;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.model.TransferModel;
import org.acme.blockchain.transaction.model.UtxoModel;
import org.acme.blockchain.transaction.repository.UtxoRepository;
import org.acme.blockchain.transaction.service.validator.TransferValidator;

import java.util.List;

@ApplicationScoped
public class TransferInputsValidator implements TransferValidator {

    private final TransferCacheService cache;

    private final UtxoRepository utxoRepository;

    @Inject
    public TransferInputsValidator(TransferCacheService cache, UtxoRepository utxoRepository) {
        this.cache = cache;
        this.utxoRepository = utxoRepository;
    }

    @Override
    public void validate(TransferModel transfer, TransactionValidationModel validationResult) {
        List<UtxoModel> inputs = transfer.getInputs();

        if (inputs == null) {
            validationResult.addFailure(transfer + " Inputs are null.");
        }

        if (inputs != null && inputs.isEmpty()) {
            validationResult.addFailure(transfer + " Inputs are empty.");
        }

        if (inputs != null && !inputs.isEmpty()) {

            boolean isAfforded = true;

            Coin totalValueOfInputs = transfer.getTotalValueOfInputs();
            Coin totalRequired = transfer.getTotalRequired();

            if (totalValueOfInputs.isLessThan(totalRequired)) {
                validationResult.addFailure(transfer + " Inputs cannot afford transaction ("
                        + totalRequired + "): " + totalValueOfInputs);
                isAfforded = false;
            }

            if (isAfforded) {

                for (UtxoModel input : inputs) {

                    if (input.getId() != null && cache.containsInput(input.getId())) {
                        validationResult.addFailure(input + " Input double spent pending mining.");
                        break;
                    }

                    if (utxoRepository.isSpent(input.getId())) {
                        validationResult.addFailure(input + " Input double spent.");
                        break;
                    }

                    if (!transfer.getSenderAddress().equals(input.getRecipientAddress())) {
                        validationResult.addFailure(input + " Input recipient does not equal transaction sender ("
                                + transfer.getSenderAddress() + "): " + input.getRecipientAddress());
                    }

                    if (transfer.getCreatedAt().isBefore(input.getCreatedAt())) {
                        validationResult.addFailure(input + " Input timestamp does not precede transaction timestamp ("
                                + transfer.getCreatedAt() + "): " + input.getCreatedAt());
                    }

                    if (input.isSpent()) {
                        validationResult.addFailure(input + " Input is already spent.");
                    }
                }
            }
        }
    }
}
