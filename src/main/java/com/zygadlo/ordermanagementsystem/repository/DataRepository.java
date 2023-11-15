package com.zygadlo.ordermanagementsystem.repository;

import com.zygadlo.ordermanagementsystem.model.DataFromDatabase;
import com.zygadlo.ordermanagementsystem.model.FileSettings;
import com.zygadlo.ordermanagementsystem.model.Product;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

public interface DataRepository {

    Map<String, DataFromDatabase> getDataFromDatabase(File file, FileSettings settings) throws FileNotFoundException;

}
