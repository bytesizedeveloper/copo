package org.acme.blockchain.common.service;

import org.acme.blockchain.transaction.model.TransactionGossip;
import org.acme.blockchain.transaction.model.TransactionModel;
import org.acme.blockchain.transaction.model.UtxoModel;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@ApplicationScoped
public class TransactionCacheService {

    private final Map<String, TransactionModel> transactionByHashIdMap = new ConcurrentHashMap<>();

    private final Map<String, UtxoModel> inputByIdMap = new ConcurrentHashMap<>();

    private final Map<String, TransactionGossip> transactionGossipByHashIdMap = new ConcurrentHashMap<>();

    private final ConcurrentLinkedQueue<TransactionModel> readyToMine = new ConcurrentLinkedQueue<>();

    public void addTransaction(String hashId, TransactionModel transaction) {
        this.transactionByHashIdMap.put(hashId, transaction);
        transaction.getInputs().forEach(input -> this.inputByIdMap.put(input.getUtxoId(), input));
    }

    public void removeTransaction(TransactionModel transaction) {
        this.transactionByHashIdMap.remove(transaction.getHashId());
        transaction.getInputs().forEach(input -> this.inputByIdMap.remove(input.getUtxoId()));
        this.transactionGossipByHashIdMap.remove(transaction.getHashId());
    }

    public boolean containsTransaction(String hashId) {
        return this.transactionByHashIdMap.containsKey(hashId);
    }

    public boolean containsInput(String hashId) {
        return this.inputByIdMap.containsKey(hashId);
    }

    public void addGossip(String hashId, TransactionGossip gossip) {
        this.transactionGossipByHashIdMap.put(hashId, gossip);
    }

    public TransactionGossip getOrDefault(String hashId) {
        return this.transactionGossipByHashIdMap.getOrDefault(hashId, new TransactionGossip());
    }

    public void readyToMine(TransactionModel transaction) {
        this.readyToMine.add(transaction);
        this.transactionGossipByHashIdMap.remove(transaction.getHashId());
    }

    public List<TransactionModel> getReadyToMine() {
        return readyToMine.stream().toList();
    }
}
