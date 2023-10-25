package com.zygadlo.ordermanagementsystem.repository;

import com.zygadlo.ordermanagementsystem.model.FileSettings;
import com.zygadlo.ordermanagementsystem.model.Product;

import java.io.File;
import java.util.Map;

public class ExcelRepository implements DataRepository{

    //TODO: finish this method for excel repo(Lemonex)
    @Override
    public Map<String, Product> getDataFromDatabase(File file, FileSettings settings) {
        return null;
    }
}
