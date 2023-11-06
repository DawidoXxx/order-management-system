package com.zygadlo.ordermanagementsystem.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "databaseUpdateTime")
public class UpdateSellersDate {
    private String name;
    private String databaseUpdateTime;

    public UpdateSellersDate(String name, String databaseUpdateTime) {
        this.name = name;
        this.databaseUpdateTime = databaseUpdateTime;
    }
}
