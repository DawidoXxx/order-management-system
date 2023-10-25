package com.zygadlo.ordermanagementsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "Seller")
public class Seller {

    @Id
    private String id;
    private String name;
    private int priority;
    private String extension;

    public Seller() {
    }

    public Seller(String name, int priority, String extension) {
        this.name = name;
        this.priority = priority;
        this.extension = extension;
    }
}
