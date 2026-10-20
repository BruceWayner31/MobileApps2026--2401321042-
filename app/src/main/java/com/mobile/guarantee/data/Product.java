package com.mobile.guarantee.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String store;
    public long purchaseDate;      // millis
    public long warrantyEndDate;   // millis
    public double price;
    public String receiptUri;      // може да е null
    public String barcode;         // може да е null
}