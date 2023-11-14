package com.zygadlo.ordermanagementsystem.service;

import com.zygadlo.ordermanagementsystem.exception.NoFileStructureFoundException;
import com.zygadlo.ordermanagementsystem.exception.StorageException;
import com.zygadlo.ordermanagementsystem.model.*;
import com.zygadlo.ordermanagementsystem.repository.*;
import org.apache.logging.slf4j.SLF4JLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ProductService {

    private static final String EXCEL_EXTENSION = ".xlsx";
    private static final String CSV_EXTENSION = ".csv";
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Value("${upload.database}")
    public String databaseFilesPath;
    private final FileStructureRepository fileStructureRepository;
    private final CsvRepository csvRepository;
    private final ExcelRepository excelRepository;
    private final ProductsRepository productRepository;
    private final SellerRepository sellerRepository;
    private final DataBaseUpdateInfo dataBaseUpdateInfo;

    public ProductService(FileStructureRepository fileStructureRepository, CsvRepository csvRepository,
                          ExcelRepository excelRepository, ProductsRepository productRepository,
                          SellerRepository sellerRepository, DataBaseUpdateInfo dataBaseUpdateInfo) {
        this.fileStructureRepository = fileStructureRepository;
        this.csvRepository = csvRepository;
        this.excelRepository = excelRepository;
        this.productRepository = productRepository;
        this.sellerRepository = sellerRepository;
        this.dataBaseUpdateInfo = dataBaseUpdateInfo;
    }

    public Map<String,DataFromDatabase> readProductsFromDatabaseFiles(File database,FileSettings fileSettings) throws StorageException,NoFileStructureFoundException{
        if (fileSettings==null)
            throw new NoFileStructureFoundException("There is no "+database.getName().split("\\.")[0]+" DataBase file structure");

        String extension = database.getName().substring(database.getName().lastIndexOf("."));

        //getting right repository for given database file
        return switch (extension){
            case EXCEL_EXTENSION -> excelRepository.getDataFromDatabase(database,fileSettings);
            case CSV_EXTENSION -> csvRepository.getDataFromDatabase(database,fileSettings);
            default -> throw new StorageException("Unsupported database file format: "+extension);
        };
    }

    public File[] getDataBaseFiles(){
        File directory = new File(databaseFilesPath);
        return Optional.ofNullable(directory.listFiles()).orElse(new File[0]);
    }

    private void saveToMongoDatabase(Map<String,DataFromDatabase> mapToSaveToDB,String name){

        //get default value of 1 if not saved,
        //priority is a second value to compare after price are the same
        int priority = sellerRepository.findByName(name) != null ?
                sellerRepository.findByName(name).getPriority() : 1;

        mapToSaveToDB.forEach((ean,data)->{
            Product product = productRepository.existsByEan(ean)?
                    productRepository.findByEan(ean):
                    new Product(ean,data.getNameOfProduct());

            ProductFromSeller productFromSeller = new ProductFromSeller(name,priority,data.getPrice(),data.getSallersCode());
            product.addProductFromSeller(productFromSeller);
            product.addNameToSearchField(data.getNameOfProduct());
            productRepository.save(product);
        });
    }

    public void updateProducts(LocalDateTime now){
        productRepository.deleteAll();
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm");

        try {
            File[] databases = getDataBaseFiles();
            if (databases==null||databases.length==0) {
                logger.warn("No database files found for update");
                return;
            }

            for (File database:databases){
                String nameOfDatabase = database.getName().split("\\.")[0];
                FileSettings fileSettings = fileStructureRepository.findByFileName(nameOfDatabase+"DB");
                Map<String, DataFromDatabase> productMap = readProductsFromDatabaseFiles(database,fileSettings);
                saveToMongoDatabase(productMap,nameOfDatabase);
                dataBaseUpdateInfo.save(new UpdateSellersDate(nameOfDatabase,(dtf.format(now))));
            }
        }catch (StorageException | NoFileStructureFoundException stEx){
            logger.error("An error occurred during product update", stEx);
        }
    }
}

//    private List<FileSettings> getNeededFileSettings(File[] databases){
//        List<FileSettings> returnList = new ArrayList<>();
//        for (File file:databases) {
//            returnList.add(fileStructureRepository.findByFileName(file.getName().split("\\.")[0]));
//        }
//        return returnList;
//    }