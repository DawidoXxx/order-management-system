package com.zygadlo.ordermanagementsystem.service;

import com.zygadlo.ordermanagementsystem.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ProductService {

    @Value("${upload.database}")
    private String databaseFilesPath;


    private void readProductsFromDatabaseFiles() throws StorageException{
        File[] databases = getDataBaseFiles();
        if (databases==null)
            throw new StorageException("There is no database files");


    }

    private File[] getDataBaseFiles(){
        File directory = new File(databaseFilesPath);
        return directory.listFiles();
    }

    private void saveToMongoDatabase(){

    }

    public void updateProducts(){

    }
}
