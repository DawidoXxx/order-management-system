package com.zygadlo.ordermanagementsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFromSeller implements Comparable{

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

    //compare by price if even compare by priority
    @Override
    public int compareTo(Object productToCompare) {
        ProductFromSeller product = (ProductFromSeller)productToCompare;
        if (product.price>this.price)
            return -1;
        else if(product.price<this.price)
            return 1;
        else{
            if (product.priority>this.getPriority())
                return -1;
            else
                return 1;
        }
    }
}
