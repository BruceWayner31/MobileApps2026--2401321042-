package com.mobile.guarantee.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    long insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM products ORDER BY warrantyEndDate ASC")
    LiveData<List<Product>> getAll();

    @Query("SELECT * FROM products WHERE id = :id")
    LiveData<Product> getById(long id);
}