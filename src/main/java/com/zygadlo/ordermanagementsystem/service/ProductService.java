package com.zygadlo.ordermanagementsystem.service;

import com.zygadlo.ordermanagementsystem.exception.NoFileStructureFoundException;
import com.zygadlo.ordermanagementsystem.exception.StorageException;
import com.zygadlo.ordermanagementsystem.model.FileSettings;
import com.zygadlo.ordermanagementsystem.model.Product;
import com.zygadlo.ordermanagementsystem.repository.CsvRepository;
import com.zygadlo.ordermanagementsystem.repository.ExcelRepository;
import com.zygadlo.ordermanagementsystem.repository.FileStructureRepository;
import com.zygadlo.ordermanagementsystem.repository.ProductsRepository;
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

    public ProductService(FileStructureRepository fileStructureRepository, CsvRepository csvRepository,
                          ExcelRepository excelRepository, ProductsRepository productRepository) {
        this.fileStructureRepository = fileStructureRepository;
        this.csvRepository = csvRepository;
        this.excelRepository = excelRepository;
        this.productRepository = productRepository;
    }

    private void readProductsFromDatabaseFiles() throws StorageException,NoFileStructureFoundException{

        //get database settings
        List<FileSettings> listOfDatabaseSettings = fileStructureRepository.findAll();

        Map<String,Product> productMap = new HashMap<>();

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
                saveToMongoDatabase(productMap);

            productMap.clear();
        }
    }

    private File[] getDataBaseFiles(){
        File directory = new File(databaseFilesPath);
        return directory.listFiles();
    }

    private void saveToMongoDatabase(Map<String,Product> mapToSaveToDB){
        mapToSaveToDB.forEach((x,y)->{
            if (productRepository.existsByEan(x)){
                Product product = productRepository.findByEan(x);
                product.addProductFromSeller(y.getProductsFromSellers().get(0));
                product.addNameToSearchField(y.getName());
            }else
                productRepository.save(y);
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
