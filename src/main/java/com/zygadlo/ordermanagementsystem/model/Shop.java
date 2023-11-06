package com.zygadlo.ordermanagementsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "shops")
public class Shop {

    @Id
    private String id;
    private String name;

    public Shop(String name) {
        this.name = name;
    }
}
