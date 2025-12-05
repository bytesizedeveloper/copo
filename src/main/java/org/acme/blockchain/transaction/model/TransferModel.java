package org.acme.blockchain.transaction.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.acme.blockchain.common.model.Coin;
import org.acme.blockchain.transaction.model.enumeration.OutputIndex;
import org.acme.blockchain.transaction.model.enumeration.TransactionStatus;

import java.math.BigDecimal;
import java.util.List;

@Getter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class TransferModel extends TransactionModel {

    @Override
    public void generateOutputs() {
        Coin availableFunds = this.getTotalValueOfInputs();
        Coin totalRequired = this.getTotalRequired();

        Coin change = availableFunds.subtract(totalRequired);

        UtxoId utxoForRecipientId = new UtxoId(this.hashId, OutputIndex.RECIPIENT);
        UtxoModel utxoForRecipient = UtxoModel.builder()
                .id(utxoForRecipientId)
                .recipientAddress(this.recipientAddress)
                .amount(this.amount)
                .createdAt(this.createdAt)
                .isSpent(false)
                .build();

        if (change.isPositive()) {
            UtxoId utxoForSenderId = new UtxoId(this.hashId, OutputIndex.SENDER);
            UtxoModel utxoForSender = UtxoModel.builder()
                    .id(utxoForSenderId)
                    .recipientAddress(this.senderAddress)
                    .amount(change)
                    .createdAt(this.createdAt)
                    .isSpent(false)
                    .build();

            this.outputs = List.of(utxoForRecipient, utxoForSender);
        } else {
            this.outputs = List.of(utxoForRecipient);
        }
    }

    public Coin getTotalRequired() {
        return this.amount.add(this.fee);
    }

    public Coin getTotalValueOfInputs() {
        return this.inputs.stream().map(UtxoModel::getAmount).reduce(new Coin(BigDecimal.ZERO), Coin::add);
    }

    public boolean isTerminal() {
        return TransactionStatus.getTerminal().contains(this.status);
    }
}
