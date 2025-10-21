package org.acme.blockchain.network;

import org.acme.blockchain.block.model.BlockModel;
import org.acme.blockchain.block.repository.BlockRepository;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.UtxoModel;
import org.acme.blockchain.transaction.repository.TransactionRepository;
import org.acme.blockchain.transaction.repository.UtxoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TempNetwork {

    private final BlockRepository blockRepository;

    private final TransactionRepository transactionRepository;

    private final UtxoRepository utxoRepository;

    @Inject
    public TempNetwork(
            BlockRepository blockRepository,
            TransactionRepository transactionRepository,
            UtxoRepository utxoRepository
    ) {
        this.blockRepository = blockRepository;
        this.transactionRepository = transactionRepository;
        this.utxoRepository = utxoRepository;
    }

    public void broadcast(BlockModel blockModel) {
        blockRepository.insert(blockModel);
        transactionRepository.batchInsert(blockModel.getTransactions());
        for (TransactionModel transaction : blockModel.getTransactions()) {
            if (!transaction.isReward()) {
                utxoRepository.updateUnspentUtxoToSpent(transaction.getInputs().stream().map(UtxoModel::getId).toList());
            }
            utxoRepository.batchInsert(transaction.getOutputs());
        }
    }
}
