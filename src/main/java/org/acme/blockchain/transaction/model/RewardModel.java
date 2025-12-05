package org.acme.blockchain.transaction.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.acme.blockchain.common.model.Coin;
import org.acme.blockchain.transaction.model.enumeration.OutputIndex;
import org.acme.blockchain.transaction.model.enumeration.TransactionType;

import java.util.List;

@Getter
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class RewardModel extends TransactionModel {

    public RewardModel() {
        this.type = TransactionType.REWARD;
        this.senderAddress = this.recipientAddress;
        this.senderPublicKeyEncoded = new byte[]{0};
        this.fee = Coin.ZERO;
        this.inputs = List.of();
        this.signature = TransactionSignature.REWARD_SIGNATURE;
    }

    @Override
    public void generateOutputs() {
        UtxoId id = new UtxoId(this.hashId, OutputIndex.RECIPIENT);

        UtxoModel output = UtxoModel.builder()
                .id(id)
                .recipientAddress(this.recipientAddress)
                .amount(this.amount)
                .createdAt(this.createdAt)
                .isSpent(false)
                .build();

        this.outputs = List.of(output);
    }
}
