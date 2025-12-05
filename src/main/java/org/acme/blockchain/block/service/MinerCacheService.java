package org.acme.blockchain.block.service;

import io.vertx.core.impl.ConcurrentHashSet;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import org.acme.blockchain.common.model.Address;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A thread-safe, application-scoped cache service for tracking the status of active miners
 * and coordinating the outcome of the current Proof-of-Work (PoW) attempt.
 * <p>
 * As an {@code @ApplicationScoped} bean, it maintains a single, global state of miners
 * and the {@code isMined} flag, which is essential for synchronization across
 * multiple mining threads/pulses orchestrated by the {@code MinerService}.
 */
@ApplicationScoped
public class MinerCacheService {

    /**
     * A thread-safe set holding the wallet addresses of all nodes currently participating
     * in the mining process. Used by the {@code MinerService} to determine which miners
     * should receive the next PoW task.
     */
    @Getter
    private final Set<Address> isMining = new ConcurrentHashSet<>();

    /**
     * An atomic flag used to indicate whether the current mining block has already been
     * successfully mined by any node (local or remote). This is crucial for stopping
     * redundant PoW attempts quickly across concurrent threads.
     */
    private final AtomicBoolean isPulseMined = new AtomicBoolean(false);

    /**
     * Adds a wallet address to the set of active miners.
     *
     * @param address The public wallet address to register for mining.
     */
    public void add(Address address) {
        this.isMining.add(address);
    }

    /**
     * Removes a wallet address from the set of active miners.
     *
     * @param address The public wallet address to remove (i.e., stop mining).
     */
    public void remove(Address address) {
        this.isMining.remove(address);
    }

    /**
     * Checks if a given wallet address is currently registered as an active miner.
     *
     * @param address The public wallet address to check.
     * @return {@code true} if the address is currently mining, {@code false} otherwise.
     */
    public boolean contains(Address address) {
        return this.isMining.contains(address);
    }

    /**
     * Atomically sets the {@code isMined} flag.
     * <p>
     * Typically set to {@code true} when a block is successfully mined locally or received
     * from the network, signaling all running mining threads to stop their current PoW attempt.
     *
     * @param value The new address for the flag (e.g., {@code true} to signal a solved block).
     */
    public void setIsPulseMined(boolean value) {
        this.isPulseMined.set(value);
    }

    /**
     * Atomically retrieves the current state of the {@code isMined} flag.
     *
     * @return {@code true} if a block has been mined during the current pulse, {@code false} otherwise.
     */
    public boolean getIsPulseMined() {
        return this.isPulseMined.get();
    }
}
