package com.microservices.snapshot.isolation.domain;

import lombok.Getter;
import lombok.ToString;

import java.util.Optional;

/**
 * @author Andrey Sukhovitsky
 */
@ToString
public class Row {
    @Getter
    private final int id;

    @Getter
    private final Content content;

    @Getter
    private final int createdBy;

    private Optional<Integer> deletedBy = Optional.empty();

    public Row(int id, Content content, int createdBy) {
        this.id = id;
        this.content = content;
        this.createdBy = createdBy;
    }

    public String read() {
        return content.getContent();
    }

    public void delete(int txId) {
        deletedBy = Optional.of(txId);
    }

    public boolean isDelete() {
        return deletedBy.isPresent();
    }

}
