package com.zygadlo.ordermanagementsystem.repository;


import com.zygadlo.ordermanagementsystem.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProductsRepository extends MongoRepository<Product,String> {

    Product findByEan(String ean);
    boolean existsByEan(String ean);

    //use regex to find product using searchNames field which contains different name
    //from different seller
    List<Product> findBySearchNamesRegex(String regex);

    @Query("{'name' : {$regex: ?0}}")
    List<Product> getAllProductsByName(String regex);

    @Query("{'ean' : {$regex: ?0}}")
    List<Product> getAllProductsByEan(String regex);

    //regex for get product by searching in their names for some string like soup or bread
    //we should pass as parameter "^.*stringToFind.*$"
    //"^.*700111.*$"
    //{'ean' : {$regex: "^.*700111.*$"}}
    //(?i)(?:test.*long|long.*test)
}
