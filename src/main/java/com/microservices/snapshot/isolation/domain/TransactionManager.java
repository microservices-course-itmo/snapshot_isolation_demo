package com.microservices.snapshot.isolation.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Andrey Sukhovitsky
 */
@Service
@Slf4j
public class TransactionManager {
    private final List<Transaction> currentlyRunning = new ArrayList<>();
    private final List<Transaction> abortedTransaction = new ArrayList<>();

    public synchronized Transaction createTransaction() {
        Transaction transaction = new Transaction(
                new TransactionalContext(
                        Collections.unmodifiableList(new ArrayList<>(currentlyRunning)),
                        Collections.unmodifiableList(new ArrayList<>(abortedTransaction))
                )
        );

        log.info("Currently running txs: {}", currentlyRunning.stream()
                .map(Transaction::getId).map(Object::toString).collect(Collectors.joining(", ")));

        log.info("Created transaction id: {}", transaction.getId());

        currentlyRunning.add(transaction);

        return transaction;
    }

    public synchronized void rollbackTransaction(Transaction transaction) {
        // rollback writes
        abortedTransaction.add(transaction);
    }

    public synchronized void commitTransaction(Transaction transaction) {
        log.info("Committed tx id: {}", transaction.getId());
        currentlyRunning.remove(transaction);
    }

    public Transaction getById(int id) {
        return currentlyRunning.stream()
                .filter(tx -> id == tx.getId())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no such tx"));
    }

    public List<Transaction> getCurrentlyRunning() {
        return currentlyRunning;
    }
}
