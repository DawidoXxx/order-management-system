package com.zygadlo.ordermanagementsystem.repository;

import com.zygadlo.ordermanagementsystem.model.FileSettings;
import com.zygadlo.ordermanagementsystem.model.Product;

import java.io.File;
import java.util.Map;

public interface DataRepository {

    Map<String, Product> getDataFromDatabase(File file, FileSettings settings);

}
