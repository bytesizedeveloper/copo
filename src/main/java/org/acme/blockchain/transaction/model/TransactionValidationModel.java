package org.acme.blockchain.transaction.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionValidationModel {

    private final List<String> failures = new ArrayList<>();

    public void addFailure(String failureMessage) {
        this.failures.add(failureMessage);
    }

    public boolean isSuccessful() {
        return failures.isEmpty();
    }

    public List<String> getFailures() {
        return Collections.unmodifiableList(failures);
    }

    @Override
    public String toString() {
        return isSuccessful() ? "Success" : String.join("\n", failures);
    }
}
