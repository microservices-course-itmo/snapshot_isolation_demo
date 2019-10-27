package com.microservices.snapshot.isolation.domain;

import lombok.EqualsAndHashCode;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Andrey Sukhovitsky
 */
@EqualsAndHashCode(of = "id")
public class Transaction {
    private static final AtomicInteger txIdGenerator = new AtomicInteger(0);

    private final int id = txIdGenerator.getAndIncrement();
    private final TransactionalContext context;

    public Transaction(TransactionalContext context) {
        this.context = context;
    }

    public int getId() {
        return id;
    }

    public TransactionalContext getContext() {
        return context;
    }
}
