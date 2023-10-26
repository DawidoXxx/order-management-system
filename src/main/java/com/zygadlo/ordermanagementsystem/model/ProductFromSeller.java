package com.zygadlo.ordermanagementsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductFromSeller {

    private String sellerName;
    private int priority;
    private Double price;
    private String sellersCode;
    private int amountInPack;
    private String codeEanAll;

    public ProductFromSeller(String sellerName, int priority, Double price, String sellersCode) {
        this.sellerName = sellerName;
        this.priority = priority;
        this.price = price;
        this.sellersCode = sellersCode;
    }
}
