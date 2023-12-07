package com.zygadlo.ordermanagementsystem.service;

import com.zygadlo.ordermanagementsystem.constans.NamesConstans;
import com.zygadlo.ordermanagementsystem.model.Product;
import com.zygadlo.ordermanagementsystem.model.ProductWithPricesToDisplay;
import com.zygadlo.ordermanagementsystem.repository.ProductsRepository;
import com.zygadlo.ordermanagementsystem.repository.SellerRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FindProductService {

    private final ProductsRepository productsRepository;
    private MongoTemplate mongoTemplate;
    private Map<String,Integer> mapOfSeller;

    public FindProductService(ProductsRepository productsRepository,MongoTemplate mongoTemplate) {
        this.productsRepository = productsRepository;
        this.mongoTemplate = mongoTemplate;
        mapOfSeller = new HashMap<>();
        mapOfSeller.put(NamesConstans.SPI,0);
        mapOfSeller.put(NamesConstans.MAS,1);
        mapOfSeller.put(NamesConstans.WAB,2);
        mapOfSeller.put(NamesConstans.LEM,3);
        mapOfSeller.put(NamesConstans.KAM,4);
        mapOfSeller.put(NamesConstans.STA,5);
    }

    public List<ProductWithPricesToDisplay> findProductsByName(String name){
        List<ProductWithPricesToDisplay> list;
        //Query query = new Query();
        //query.addCriteria(TextCriteria.forDefaultLanguage().matchingAny(name));
        //Long count = mongoTemplate.count(query, Product.class);
//        query.addCriteria(TextCriteria.forDefaultLanguage().matchingPhrase(name));
//        query.addCriteria(TextCriteria.forDefaultLanguage().matching(name));
        //TODO: return mapped list not one below //changeObjectToDisplay()
//        return template.find(query,ProductFromSaler.class);
//        return changeObjectToDisplay(template.find(query,ProductFromSaler.class));
        if (name.contains(" "))
            name = prepareSearchString(name);

        list = changeObjectToDisplay(productsRepository.findBySearchNamesRegex(name));
        if (list == null|| list.isEmpty())
            list = changeObjectToDisplay(productsRepository.findBySearchNamesRegex(name.toUpperCase()));

        return list;
    }

    public List<ProductWithPricesToDisplay> changeObjectToDisplay(List<Product> list){
        List<ProductWithPricesToDisplay> listToReturn = new ArrayList<>();
        for (Product prod: list) {
            listToReturn.add(new ProductWithPricesToDisplay(prod,mapOfSeller));
        }
        return listToReturn;
    }

    private String prepareSearchString(String userInput) {
        //return ".*(?:" + userInput.replace(" ", "|") + ").*";
        String[] words = userInput.split(" ");
        StringBuilder returnRegex = new StringBuilder("^");
        for (String word:words) {
            returnRegex.append("(?=.*").append(word).append(")");
        }
        returnRegex.append(".*$");
        return returnRegex.toString();
    }

    public List<ProductWithPricesToDisplay> findProductsByEanRegex(String eanRegex){
        return changeObjectToDisplay(productsRepository.getAllProductsByEan(eanRegex));
    }
}
