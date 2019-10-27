package com.microservices.snapshot.isolation.domain;

import java.util.Collections;
import java.util.List;

/**
 * @author Andrey Sukhovitsky
 */
public class TransactionalContext {
    private final List<Transaction> runningInParallel;
    private final List<Transaction> abortedTransaction;

    public TransactionalContext(List<Transaction> runningInParallel, List<Transaction> abortedTransaction) {
        this.runningInParallel = runningInParallel;
        this.abortedTransaction = abortedTransaction;
    }

    public List<Transaction> getRunningInParallel() {
        return Collections.unmodifiableList(runningInParallel);
    }

    public List<Transaction> getAbortedTransaction() {
        return Collections.unmodifiableList(abortedTransaction);
    }
}
