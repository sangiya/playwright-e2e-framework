package com.sangiya.e2e.service;

import com.sangiya.e2e.model.Product;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductStore {

    private static final String BLANK_NAME_MESSAGE = "Product name must not be blank";

    private final List<Product> products = new CopyOnWriteArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public Product add(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(BLANK_NAME_MESSAGE);
        }
        Product product = new Product(nextId.getAndIncrement(), name.strip(), Instant.now());
        products.add(product);
        return product;
    }

    public List<Product> list() {
        return List.copyOf(products);
    }

    public void clear() {
        products.clear();
    }
}