package com.zygadlo.ordermanagementsystem.service;

import com.zygadlo.ordermanagementsystem.constans.NamesConstans;
import com.zygadlo.ordermanagementsystem.exception.FieldsMissMatchException;
import com.zygadlo.ordermanagementsystem.model.FileSettings;
import com.zygadlo.ordermanagementsystem.model.Product;
import com.zygadlo.ordermanagementsystem.model.ProductFromSeller;
import com.zygadlo.ordermanagementsystem.model.Savings;
import com.zygadlo.ordermanagementsystem.repository.ProductsRepository;
import com.zygadlo.ordermanagementsystem.repository.SavingsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class OrderService {

    @Value("${upload.orderFile}")
    private String orderFilePath;
    @Value("${create.ordersToSend}")
    private String pathToCreatedOrderFiles;
    private final SettingsService settingsService;
    private final ProductsRepository productsRepository;
    private final SavingsRepository savingsRepository;
    private final StorageService storageService;


    public OrderService(SettingsService settingsService, ProductsRepository productsRepository,
                        SavingsRepository savingsRepository, StorageService storageService) {
        this.settingsService = settingsService;
        this.productsRepository = productsRepository;
        this.savingsRepository = savingsRepository;
        this.storageService = storageService;
    }



    public void getLowestPrices(List<Product> listOfProductToOrder) {
        for (Product prod:listOfProductToOrder) {
            Collections.sort(prod.getProductsFromSellers());
        }
    }

    public List<Product> loadDataFromDB(List<Product> listOfProductToOrder) {
        Product prodFromDB;
        try {
            for (Product product:listOfProductToOrder) {
                prodFromDB=productsRepository.findByEan(product.getEan());
                //IF WE FOUND WE ADD TO ORDER OUTPUT FILE
                if (prodFromDB!=null) {
                    if (product.getName()==null && prodFromDB.getName()!=null) {
                        product.setName(prodFromDB.getName());
                    }
                    //WE NEED CHECK IF IN DIFFERENT DATABASE THIS PRODUCT IS WITH DIFFERENT CODE
                    product.addProductsFromSellers(prodFromDB.getProductsFromSellers());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listOfProductToOrder;
    }

    public List<Product> loadDataFromOrderFile() {
        FileSettings fileSettings = settingsService.findByNameOfFile(NamesConstans.ORDER_TO_PROCESS);
        String separator = fileSettings!=null? fileSettings.getSeparator() : ";";
        File file = new File(orderFilePath+"order.txt");
        List<Product> readedProductList = new ArrayList<>();

        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(file.getAbsolutePath()));

            String readedLine;
            while((readedLine = fileReader.readLine())!=null) {
                if (readedLine.split(separator)[0].equals("")||readedLine.split(separator)[0].equals(" "))
                    continue;
                readedProductList.add(readProductFromOrderFile(readedLine,fileSettings));
            }
            fileReader.close();
        }
        catch (IOException ex){
            ex.getStackTrace();
        }
        catch (FieldsMissMatchException ex){
            ex.getMessage();
        }

        return readedProductList;
    }

    private Product readProductFromOrderFile(String readedLine, FileSettings fileSettings) throws FieldsMissMatchException{
        Product product = new Product();
        if (fileSettings != null) {
            String[] readedFields = readedLine.split(fileSettings.getSeparator());

            fileSettings.getFieldsOrderMap().forEach((v,k)->{
                if (!(v>=readedFields.length)) {

                    if (k.equals("name"))
                        product.setName(readedFields[v]);
                    if (k.equals("amount"))
                        product.setAmount((int) Double.parseDouble(readedFields[v]));
                    if (k.equals("ean"))
                        product.setEan(readedFields[v]);
                }
                //else throw exception about input order file columns dont match to settings
            });
            return product;
        }
        else {
            String[] readedFields = readedLine.split(";");
            if (isThisANumber(readedFields[2])) {
                int liczba = (int) Double.parseDouble(readedFields[2]);
                return new Product(readedFields[0], readedFields[1], liczba);
            }
            else{
                throw new FieldsMissMatchException("there are difference between user order file and saved file structure");
            }
        }
    }

    private boolean isThisANumber(String word){
        try {
            Double.parseDouble(word);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public double calculateAndSaveSavings(List<Product> productList) {
        double savings = 0;
        for (Product product:productList) {
            //get difference between most expensive one and cheapest one
            if (product.getProductsFromSellers().size()>1) {
                List<ProductFromSeller> list = product.getProductsFromSellers();
                savings += list.get(list.size() - 1).getPrice()
                        - list.get(0).getPrice();
            }
        }
        Savings savingsObject;

        String month = getTodayDate();

        if (savingsRepository.findByMonthOfSavings(month).isPresent()) {
            savingsObject = savingsRepository.findByMonthOfSavings(month).get();
            savingsObject.addCash(savings);
        }
        else
            savingsObject = new Savings(month,savings);

        savingsRepository.save(savingsObject);

        return savings;
    }

    public String getTodayDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return formatter.format(localDateTime);
    }

    private Map<String,FileSettings> loadDataForFilesStructure() {
        List<FileSettings> list = settingsService.findAllSettings();
        if (list==null||list.isEmpty())
            return mapFilesStrucure(new ArrayList<>());
        return mapFilesStrucure(list);
    }

    private HashMap<String, FileSettings> mapFilesStrucure(List<FileSettings> list) {
        HashMap<String,FileSettings> mapToReturn = new HashMap<>();

        for (FileSettings fileSetting:list) {
            mapToReturn.put(fileSetting.getFileName(),fileSetting);
        }

        return mapToReturn;
    }

    public String writeDataToOrderFile(List<String> sallersName,String shopName, List<Product> productList) {

        Map<String,FileSettings> fileSettingsMap = loadDataForFilesStructure();
        String date=getTodayDate();
        StringBuilder pathStringBuilder = new StringBuilder();
        pathStringBuilder.append(pathToCreatedOrderFiles);
        pathStringBuilder.append(date);

        storageService.createIfNotExist(pathStringBuilder.toString());
        pathStringBuilder.append("/");
        pathStringBuilder.append(shopName);
        storageService.createIfNotExist(pathStringBuilder.toString());
        File directoryWithOrderFilesToDelete = new File(pathStringBuilder.toString());

        File[] filesToDelete = directoryWithOrderFilesToDelete.listFiles();
        if (filesToDelete!=null&&filesToDelete.length!=0)
        for (File fileToDel:filesToDelete) {
            fileToDel.delete();
        }

        //STWÓRZ PLIK WYNIKOWY GŁÓWNY I ZAPISZ W NIM WYNIKI
        File file = new File(pathStringBuilder+"/order.csv");
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
            FileSettings fileMainStrucrture = fileSettingsMap.get(NamesConstans.ORDER_TO_SEND_MAIN);

            //TODO: change method of writing to the files
            for (Product prod : productList) {
                if (fileMainStrucrture!=null){
                    writeToMainFile(prod,fileMainStrucrture,sallersName,bufferedWriter);
                }
                else{
                    Map<Integer,String> defaultFieldOrderMap = new HashMap<>();
                    defaultFieldOrderMap.put(0,"EAN");
                    defaultFieldOrderMap.put(1,"NAME");
                    defaultFieldOrderMap.put(2,"AMOUNT");
                    writeToMainFile(prod,new FileSettings("DEFAULT",";",".csv",
                                    defaultFieldOrderMap), sallersName, bufferedWriter);
                    bufferedWriter.newLine();
                }
            }

            bufferedWriter.close();
        }
        catch (IOException ex){
            ex.getStackTrace();
        }
        return pathStringBuilder.toString();
    }

    private void writeToMainFile(Product prod,FileSettings fileStructure,List<String> sellersName,BufferedWriter bw) throws IOException{
        writeToFile(prod,fileStructure,bw);
        writePricesToFile(prod,sellersName,bw);
    }

    private void writePricesToFile(Product prod, List<String> sellersName, BufferedWriter bw) throws IOException{
        for (ProductFromSeller product : prod.getProductsFromSellers()) {
            if (sellersName.contains(product.getSellerName())) {
                bw.write(product.getSellerName());
                bw.write(";");
                bw.write(String.valueOf(product.getPrice()));
                bw.write(";");
            }
        }
    }

    public void writeToFile(Product product, FileSettings fileStructure, BufferedWriter bufferedWriter) throws IOException {

        Map<Integer,String> map = fileStructure.getFieldsOrderMap();

        //TODO:zmienić nie można być pewnym co do kolejności entry w hashmap
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            Integer k = entry.getKey();
            String v = entry.getValue();
            switch (v) {
                case "EAN":
                    bufferedWriter.write(product.getEan());
                    break;
                case "NAME":
                    bufferedWriter.write(product.getName());
                    break;
                case "AMOUNT":
                    bufferedWriter.write(String.valueOf(product.getAmount()));
                    break;
                case "PRICE":
                    bufferedWriter.write(product.getProductsFromSellers().get(0).getPrice().toString().replace(".", ","));
                    break;
                case "SELLERCODE":
                    bufferedWriter.write(product.getProductsFromSellers().get(0).getSellersCode());
                    break;
                default:
                    bufferedWriter.write(" ");
                    break;
            }
            bufferedWriter.write(fileStructure.getSeparator());
        }
    }

    public void writeDataToOrderFileToDifferentSellers(List<String> sellers, String shopName,
                                                      List<Product> productList) {
        String date = getTodayDate();
        Map<String,FileSettings> fileSettingsMap = loadDataForFilesStructure();
        HashMap<String,String> mapOfNames = new HashMap<>();
        sellers.add(NamesConstans.NOF);
        mapOfNames.put(NamesConstans.SPI,NamesConstans.ORDER_TO_SEND_SPIZARNIA);
        mapOfNames.put(NamesConstans.MAS,NamesConstans.ORDER_TO_SEND_MASTER);
        mapOfNames.put(NamesConstans.WAB,NamesConstans.ORDER_TO_SEND_WABAR);
        mapOfNames.put(NamesConstans.LEM,NamesConstans.ORDER_TO_SEND_LEMONEX);
        mapOfNames.put(NamesConstans.STA,NamesConstans.ORDER_TO_SEND_STANRO);
        mapOfNames.put(NamesConstans.KAM,NamesConstans.ORDER_TO_SEND_KAMIX);

        Map<Integer,String> defaultFieldOrderMap = new HashMap<>();
        defaultFieldOrderMap.put(0,"ean");
        defaultFieldOrderMap.put(1,"name");
        defaultFieldOrderMap.put(2,"amount");

        Map<String,List<Product>> mapOfListForEachSeller = createMapOfListForEachSeller(sellers,productList);

        try {

            for (String seller : sellers) {
                 BufferedWriter bw = new BufferedWriter(new FileWriter(pathToCreatedOrderFiles + date + "/" + shopName + "/" + seller + ".csv"));
                List<Product> specificProductList = mapOfListForEachSeller.get(seller);
                FileSettings fileSettings = null;
                if (fileSettingsMap.containsKey(mapOfNames.get(seller)))
                    fileSettings = fileSettingsMap.get(mapOfNames.get(seller));
                if (fileSettings==null)
                    fileSettings = new FileSettings("DEFAULT",";",".csv",defaultFieldOrderMap);
                for (Product product:specificProductList){
                    bw.write(buildLineForBufferWriter(product,fileSettings));
                    bw.newLine();
                }
                bw.close();
            }
        } catch (IOException exception){
            System.out.println(exception.getMessage());
        }

        productList.clear();
    }

    private String buildLineForBufferWriter(Product product, FileSettings fileStructure){

        Map<Integer,String> map = fileStructure.getFieldsOrderMap();
        StringBuilder stringBuilder = new StringBuilder();

        map.forEach((k,v)->{
            switch(v){
                case "ean":
                    stringBuilder.append(product.getEan());
                    break;
                case "name":
                    stringBuilder.append(product.getName());
                    break;
                case "amount":
                    stringBuilder.append(product.getAmount());
                    break;
                case "price":
                    stringBuilder.append(product.getProductsFromSellers().get(0).getPrice().toString().replace(".", ","));
                    break;
                case "sallerCode":
                    stringBuilder.append(product.getProductsFromSellers().get(0).getSellersCode());
                    break;
                default:
                    stringBuilder.append(" ");
                    break;
            }
            stringBuilder.append(fileStructure.getSeparator());
        });

        return stringBuilder.toString();
    }

    private Map<String, List<Product>> createMapOfListForEachSeller(List<String> sellers,List<Product> productList) {
        Map<String,List<Product>> map = new HashMap<>();
        for (String sellerName:sellers){
            List<Product> list = productList.stream()
                    .filter(product -> product.getProductsFromSellers().size()>0)
                    .filter(product -> product.getProductsFromSellers().get(0).getSellerName().equals(sellerName))
                    .toList();
            map.put(sellerName,list);
        }
        List<Product> noFoundProductList = productList.stream()
                .filter(product -> product.getProductsFromSellers().size()==0)
                .toList();
        map.put(NamesConstans.NOF,noFoundProductList);

        return map;
    }
}
