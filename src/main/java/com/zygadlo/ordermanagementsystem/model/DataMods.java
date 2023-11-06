package com.zygadlo.ordermanagementsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataMods {
    private String name;
    private String date;
    private int priority;
}
