package com.sangiya.e2e.model;

import java.time.Instant;

public class Product {

    private final long id;
    private final String name;
    private final Instant createdAt;

    public Product(long id, String name, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}