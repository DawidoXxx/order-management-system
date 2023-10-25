package com.zygadlo.ordermanagementsystem.repository;

import com.zygadlo.ordermanagementsystem.model.FileSettings;
import com.zygadlo.ordermanagementsystem.model.Product;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.Map;

@Repository
public class CsvRepository implements DataRepository{

    @Override
    public Map<String, Product> getDataFromDatabase(File file, FileSettings settings) {
        return null;
    }
}
