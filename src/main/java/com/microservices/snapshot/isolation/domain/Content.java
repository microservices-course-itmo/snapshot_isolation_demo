package com.microservices.snapshot.isolation.domain;

import lombok.Getter;
import lombok.ToString;

/**
 * @author Andrey Sukhovitsky
 */
@Getter
@ToString
public class Content {
    private final String content;

    public Content(String content) {
        this.content = content;
    }
}
