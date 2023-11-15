package com.zygadlo.ordermanagementsystem.repository;

import com.zygadlo.ordermanagementsystem.model.DataMods;
import com.zygadlo.ordermanagementsystem.model.UpdateSellersDate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DataBaseUpdateInfo extends MongoRepository<UpdateSellersDate,String> {
}
