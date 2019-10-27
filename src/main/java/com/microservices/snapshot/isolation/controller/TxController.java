package com.microservices.snapshot.isolation.controller;

import com.microservices.snapshot.isolation.domain.Content;
import com.microservices.snapshot.isolation.domain.Row;
import com.microservices.snapshot.isolation.domain.RowGroup;
import com.microservices.snapshot.isolation.domain.Transaction;
import com.microservices.snapshot.isolation.domain.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Andrey Sukhovitsky
 */
@RestController
public class TxController {
    private Map<Integer, RowGroup> dataStore = new ConcurrentHashMap<>();

    @Autowired
    private TransactionManager manager;

    @PostMapping("tx")
    Transaction createTx() {
        return manager.createTransaction();
    }

    @GetMapping("tx")
    List<Transaction> getTxs() {
        return manager.getCurrentlyRunning();
    }

    @DeleteMapping("tx/{txId}")
    void commitTx(@PathVariable int txId) {
        manager.commitTransaction(manager.getById(txId));
    }

    @PostMapping("tx/{txId}/row")
    Row createRow(@PathVariable int txId, @RequestBody String content) {
        Transaction tx = manager.getById(txId);
        RowGroup newRowGroup = new RowGroup();
        dataStore.put(newRowGroup.getPrimaryKey(), newRowGroup);
        newRowGroup.write(new Content(content), tx);
        return newRowGroup.read(tx);
    }

    @PutMapping("tx/{txId}/row/{rowPk}")
    Row createRow(@PathVariable int txId, @PathVariable int rowPk, @RequestBody String content) {
        Transaction tx = manager.getById(txId);
        RowGroup row = dataStore.get(rowPk);
        if (row == null) {
            throw new IllegalArgumentException("There is no such row id: " + rowPk);
        }
        row.write(new Content(content), tx);
        return row.read(tx);
    }

    @GetMapping("tx/{txId}/row/{rowPk}")
    Row getRow(@PathVariable int txId, @PathVariable int rowPk) {
        Transaction tx = manager.getById(txId);
        return dataStore.get(rowPk).read(tx);
    }
}
