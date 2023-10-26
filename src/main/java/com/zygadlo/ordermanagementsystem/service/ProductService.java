package com.zygadlo.ordermanagementsystem.service;

import com.zygadlo.ordermanagementsystem.exception.NoFileStructureFoundException;
import com.zygadlo.ordermanagementsystem.exception.StorageException;
import com.zygadlo.ordermanagementsystem.model.DataFromDatabase;
import com.zygadlo.ordermanagementsystem.model.FileSettings;
import com.zygadlo.ordermanagementsystem.model.Product;
import com.zygadlo.ordermanagementsystem.model.ProductFromSeller;
import com.zygadlo.ordermanagementsystem.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    @Value("${upload.database}")
    private String databaseFilesPath;
    private FileStructureRepository fileStructureRepository;
    private CsvRepository csvRepository;
    private ExcelRepository excelRepository;
    private ProductsRepository productRepository;
    private SellerRepository sellerRepository;

    public ProductService(FileStructureRepository fileStructureRepository, CsvRepository csvRepository,
                          ExcelRepository excelRepository, ProductsRepository productRepository,
                          SellerRepository sellerRepository) {
        this.fileStructureRepository = fileStructureRepository;
        this.csvRepository = csvRepository;
        this.excelRepository = excelRepository;
        this.productRepository = productRepository;
        this.sellerRepository = sellerRepository;
    }

    private void readProductsFromDatabaseFiles() throws StorageException,NoFileStructureFoundException{

        //get database settings
        List<FileSettings> listOfDatabaseSettings = fileStructureRepository.findAll();

        Map<String, DataFromDatabase> productMap;

        //get databases from folder
        File[] databases = getDataBaseFiles();
        if (databases==null)
            throw new StorageException("There is no database files");


        for (File database:databases) {
            FileSettings fileSettings = fileStructureRepository.findByFileName(database.getName().split("//.")[0]+"DB");

            //read if its Excel file and there is specific to this name file settings else we don't know how to read file
            if (database.getName().contains(".xlms")){
                if (fileSettings==null)
                    throw new NoFileStructureFoundException("There is no "+database.getName().split("//.")[0]+" DataBase file structure");
                productMap = excelRepository.getDataFromDatabase(database,fileSettings);
            }

            //same as above just it about csv file
            if (database.getName().contains(".csv")&&fileSettings!=null){
                productMap = csvRepository.getDataFromDatabase(database,fileSettings);
                //saveToMongoDatabase(productMap);
            }
            else
                throw new NoFileStructureFoundException("There is no "+database.getName().split("//.")[0]+" DataBase file structure");

            if (!productMap.isEmpty())
                saveToMongoDatabase(productMap,database.getName().substring(0,database.getName().indexOf(".")));

            productMap.clear();
        }
    }

    private File[] getDataBaseFiles(){
        File directory = new File(databaseFilesPath);
        return directory.listFiles();
    }

    private void saveToMongoDatabase(Map<String,DataFromDatabase> mapToSaveToDB,String name){
        int priority;

        if(sellerRepository.findByName(name)!=null)
            priority = sellerRepository.findByName(name).getPriority();
        else
            priority = 1;

        mapToSaveToDB.forEach((x,y)->{
            if (productRepository.existsByEan(x)){
                Product product = productRepository.findByEan(x);
                ProductFromSeller productFromSeller = new ProductFromSeller(name,priority,y.getPrice(),y.getSallersCode());
                product.addProductFromSeller(productFromSeller);
                product.addNameToSearchField(y.getNameOfProduct());
                productRepository.save(product);
            }else {
                Product product = new Product(x, y.getNameOfProduct());
                product.addProductFromSeller(new ProductFromSeller(name,priority,y.getPrice(),y.getSallersCode()));
                productRepository.save(product);
            }
        });
    }

    public void updateProducts(){

    }

    private List<FileSettings> getNeededFileSettings(File[] databases){
        List<FileSettings> returnList = new ArrayList<>();
        for (File file:databases) {
            returnList.add(fileStructureRepository.findByFileName(file.getName().split("\\.")[0]));
        }
        return returnList;
    }

}
