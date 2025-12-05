package org.acme.blockchain.common.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.blockchain.transaction.model.TransactionHash;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.TransferGossip;
import org.acme.blockchain.transaction.model.TransferModel;
import org.acme.blockchain.transaction.model.UtxoId;
import org.acme.blockchain.transaction.model.UtxoModel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@ApplicationScoped
public class TransferCacheService {

    private final Map<TransactionHash, TransferModel> transferByHashIdMap = new ConcurrentHashMap<>();

    private final Map<UtxoId, UtxoModel> inputByIdMap = new ConcurrentHashMap<>();

    private final Map<TransactionHash, TransferGossip> transferGossipByHashIdMap = new ConcurrentHashMap<>();

    private final ConcurrentLinkedQueue<TransferModel> readyToMine = new ConcurrentLinkedQueue<>();

    public void addTransfer(TransactionHash hashId, TransferModel transfer) {
        this.transferByHashIdMap.put(hashId, transfer);
    }

    public TransferModel get(TransactionHash hashId) {
        return this.transferByHashIdMap.get(hashId);
    }

    public void removeTransfer(TransferModel transfer) {
        this.transferByHashIdMap.remove(transfer.getHashId());
        transfer.getInputs().forEach(input -> this.inputByIdMap.remove(input.getId()));
        this.transferGossipByHashIdMap.remove(transfer.getHashId());
    }

    public boolean containsTransfer(TransactionHash hashId) {
        return this.transferByHashIdMap.containsKey(hashId);
    }

    public void addInput(UtxoModel input) {
        this.inputByIdMap.put(input.getId(), input);
    }

    public boolean containsInput(UtxoId id) {
        return this.inputByIdMap.containsKey(id);
    }

    public void addGossip(TransactionHash hashId, TransferGossip gossip) {
        this.transferGossipByHashIdMap.put(hashId, gossip);
    }

    public TransferGossip getOrDefault(TransactionHash hashId) {
        return this.transferGossipByHashIdMap.getOrDefault(hashId, new TransferGossip());
    }

    public void readyToMine(TransferModel transfer) {
        this.readyToMine.add(transfer);
        this.transferGossipByHashIdMap.remove(transfer.getHashId());
    }

    public List<TransactionModel> getReadyToMine() {
        return readyToMine.stream().map(transfer -> (TransactionModel) transfer).toList();
    }
}
