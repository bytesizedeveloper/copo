package org.acme.blockchain.common.service;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DifficultyService {

    public int calculateDifficulty() {
        return 1;
    }
}
