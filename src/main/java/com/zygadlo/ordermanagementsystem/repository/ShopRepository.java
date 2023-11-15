package com.zygadlo.ordermanagementsystem.repository;

import com.zygadlo.ordermanagementsystem.model.Shop;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShopRepository extends MongoRepository<Shop,String> {
}
