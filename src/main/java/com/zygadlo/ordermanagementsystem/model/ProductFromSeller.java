package com.zygadlo.ordermanagementsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFromSeller {

    private String sellerName;
    private int priority;
    private Double price;
    private String sellersCode;
    private int amountInPack;
    private String codeEanAll;

    public ProductFromSeller setSellerName(String name){
        this.sellerName = name;
        return this;
    }

    public ProductFromSeller setPrice(Double price){
        this.price = price;
        return this;
    }

    public ProductFromSeller setPriority(int priority){
        this.priority = priority;
        return this;
    }

    public ProductFromSeller setSellerCode(String sellerCode){
        this.sellersCode = sellerCode;
        return this;
    }

    public ProductFromSeller setAmountInPack(int amountInPack){
        this.amountInPack = amountInPack;
        return this;
    }

    public ProductFromSeller setCodeEan(String ean){
        this.codeEanAll = ean;
        return this;
    }
}
