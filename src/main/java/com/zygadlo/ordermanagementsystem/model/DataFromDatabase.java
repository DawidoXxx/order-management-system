package com.zygadlo.ordermanagementsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataFromDatabase {

    private Double price;
    private String sallersCode;
    private String nameOfProduct;
    private Double amountInPack;
    private String kodEanAll;

    public DataFromDatabase updatePrice(Double price){
        this.price = price;
        return this;
    }

    public DataFromDatabase(Double price,String sallersCode){
        this.price= price;
        this.sallersCode = sallersCode;
    }

    public DataFromDatabase(Double price, String sallersCode, String nameOfProduct) {
        this.price = price;
        this.sallersCode = sallersCode;
        this.nameOfProduct = nameOfProduct;
    }

    public DataFromDatabase(Double price, String sallersCode, String nameOfProduct, Double amountInPack) {
        this.price = price;
        this.sallersCode = sallersCode;
        this.nameOfProduct = nameOfProduct;
        this.amountInPack = amountInPack;
    }
}
