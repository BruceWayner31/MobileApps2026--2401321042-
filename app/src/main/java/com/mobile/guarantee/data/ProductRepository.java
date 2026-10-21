package com.mobile.guarantee.data;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductRepository {
    private final ProductDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ProductRepository(ProductDao dao) {
        this.dao = dao;
    }

    public LiveData<List<Product>> getAll() {
        return dao.getAll();
    }

    public LiveData<Product> getById(long id) {
        return dao.getById(id);
    }

    public void insert(Product product) {
        executor.execute(() -> dao.insert(product));
    }

    public void update(Product product) {
        executor.execute(() -> dao.update(product));
    }

    public void delete(Product product) {
        executor.execute(() -> dao.delete(product));
    }
}