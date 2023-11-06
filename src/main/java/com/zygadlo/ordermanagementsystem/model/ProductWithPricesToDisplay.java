package com.zygadlo.ordermanagementsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductWithPricesToDisplay {

    private String ean;
    private String name;
    private List<ProductFromSeller> list;
    //Spiżarnia //Wabar //Master //Lemonex //Kamix //STANRO
    private double[] prices;

    public ProductWithPricesToDisplay(Product product, Map<String,Integer> map){
        this.ean = product.getEan();
        //if(product.getName()!=null)
        this.name = product.getName().replace("\"","");
        this.list = product.getProductsFromSellers();
        prices = new double[]{0.0,0.0,0.0,0.0,0.0,0.0};
        fillPricesArray(map);
    }

    //fill price array in some order. Order depends of mapOfSallersOrder.
    //we gonna leave 0.0 when this prod is not in specific saller stock or
    //when we dont add this saller database
    private void fillPricesArray(Map<String,Integer> mapOfSellersOrder){
        if (list!=null) {
            for (ProductFromSeller prod : list) {
                prices[mapOfSellersOrder.get(prod.getSellerName())]=prod.getPrice();
            }
        }
    }
}