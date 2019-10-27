package com.microservices.snapshot.isolation.domain;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Andrey Sukhovitsky
 */
@Slf4j
public class RowGroup {
    private static final AtomicInteger primaryKeyGenerator = new AtomicInteger(0);

    private final int primaryKey = primaryKeyGenerator.getAndIncrement();

    private final LinkedList<Row> rows = new LinkedList<>();

    public int getPrimaryKey() {
        return primaryKey;
    }

    public Row read(Transaction transaction) {
        List<Row> visibleRows = rows.stream()
                .filter(row -> row.getCreatedBy() < transaction.getId())
                .filter(row -> !transaction.getContext().getRunningInParallel().stream()
                        .map(Transaction::getId)
                        .collect(Collectors.toList()).contains(row.getCreatedBy())
                )
                .filter(row -> !transaction.getContext().getAbortedTransaction().stream()
                        .map(Transaction::getId)
                        .collect(Collectors.toList()).contains(row.getCreatedBy())
                )
                .collect(Collectors.toList());

        log.info("Read request: {}", rows);

        if (visibleRows.size() > 1) {
            log.error("More than one visible " + visibleRows);
            throw new IllegalStateException("More than one visible " + visibleRows);
        }

        if (visibleRows.size() == 0) {
            log.info("Row id : {} is read by tx id : {}, value = {}", primaryKey, transaction.getId(), "null");
            return new Row(primaryKey, new Content("no previous state observed"), -1);
        }

        log.info("Row id : {} is read by tx id : {}, value = {}", primaryKey, transaction.getId(), visibleRows.get(0).read());
        return visibleRows.get(0);
    }

    public synchronized void write(Content content, Transaction transaction) {

        Row previous = new Row(primaryKey, new Content("empty state"), -1);
        if (!rows.isEmpty()) {
            previous = rows.getLast();
            previous.delete(transaction.getId());
        }
        rows.addLast(new Row(primaryKey, content, transaction.getId()));

        log.info("Row id : {} is written by tx id : {}, previous value = {}, updated value = {}",
                primaryKey, transaction.getId(), previous.read(), rows.getLast().read());
    }
}
