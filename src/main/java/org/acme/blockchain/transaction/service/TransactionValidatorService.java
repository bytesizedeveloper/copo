package org.acme.blockchain.transaction.service;

import io.quarkus.arc.All;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.blockchain.transaction.model.RewardModel;
import org.acme.blockchain.transaction.model.TransactionValidationModel;
import org.acme.blockchain.transaction.model.TransferModel;
import org.acme.blockchain.transaction.service.validator.RewardValidator;
import org.acme.blockchain.transaction.service.validator.TransferValidator;

import java.util.List;

@ApplicationScoped
public class TransactionValidatorService {

    private final List<RewardValidator> rewardValidators;

    private final List<TransferValidator> transferValidators;

    @Inject
    public TransactionValidatorService(
            @All List<RewardValidator> rewardValidators,
            @All List<TransferValidator> transferValidators
    ) {
        this.rewardValidators = rewardValidators;
        this.transferValidators = transferValidators;
    }

    public void validateReward(RewardModel reward, TransactionValidationModel validationResult) {
        rewardValidators.forEach(validator -> validator.validate(reward, validationResult));
    }

    public void validateTransfer(TransferModel transfer, TransactionValidationModel validationResult) {
        transferValidators.forEach(validator -> validator.validate(transfer, validationResult));
    }
}
