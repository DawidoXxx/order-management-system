package com.zygadlo.ordermanagementsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Document(collection = "products")
public class Product {

    @Id
    private String id;
    private String ean;
    private List<ProductFromSeller> productsFromSellers;
    @TextIndexed
    private String name;
    private int amount;
    private String searchNames;

    public Product(){productsFromSellers = new ArrayList<>();}

    public Product(String ean, String name, int amount) {
        this.ean = ean;
        this.name = name;
        this.amount = amount;
        this.searchNames = "";
    }

    public void addProductFromSeller(@NonNull ProductFromSeller product){
        productsFromSellers.add(product);
    }

    //There are different names in sellers database files for the same product
    //So we gonna to put all in one field and search within this field
    public void addNameToSearchField(@NonNull String nameFromAnotherSeller){
        if (searchNames==null)
            searchNames = "";
        searchNames+=nameFromAnotherSeller+" ";
    }
}
