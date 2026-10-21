package com.mobile.guarantee.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mobile.guarantee.data.AppDatabase;
import com.mobile.guarantee.data.Product;
import com.mobile.guarantee.data.ProductRepository;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {
    private final ProductRepository repository;
    private final LiveData<List<Product>> products;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        repository = new ProductRepository(
                AppDatabase.getInstance(application).productDao());
        products = repository.getAll();
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public LiveData<Product> getProduct(long id) {
        return repository.getById(id);
    }

    public void insert(Product product) {
        repository.insert(product);
    }

    public void update(Product product) {
        repository.update(product);
    }

    public void delete(Product product) {
        repository.delete(product);
    }
}