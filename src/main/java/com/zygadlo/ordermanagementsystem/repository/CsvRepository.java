package com.zygadlo.ordermanagementsystem.repository;

import com.zygadlo.ordermanagementsystem.model.DataFromDatabase;
import com.zygadlo.ordermanagementsystem.model.FileSettings;
import com.zygadlo.ordermanagementsystem.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Repository
public class CsvRepository implements DataRepository{

    private static final Logger logger = LoggerFactory.getLogger(CsvRepository.class);

    public Map<String,DataFromDatabase> getDataFromReader(Reader reader,FileSettings settings){

        Map<String,DataFromDatabase> productMap = new HashMap<>();
        Map<String,Integer> fieldOrder;
        fieldOrder = reverseMap(settings.getFieldsOrderMap());

        BufferedReader bufferedReader;
        String readedLine;
        String[] readedFields;

        try{
            bufferedReader = new BufferedReader(reader);

            while((readedLine = bufferedReader.readLine())!=null){
                readedFields = readedLine.split(settings.getSeparator());

                if (readedFields[0].equals("EAN")||readedFields[0].equals("INDEKS"))
                    continue;

                String priceString = readedFields[fieldOrder.get(FileSettings.Fields.PRICE.name())];
                Double price = Double.parseDouble(priceString.replace(",","."));

                productMap.put(readedFields[fieldOrder.get(FileSettings.Fields.EAN.name())],
                        new DataFromDatabase(price,readedFields[fieldOrder.get(FileSettings.Fields.SELLERCODE.name())],
                                readedFields[fieldOrder.get(FileSettings.Fields.NAME.name())]));
            }

        }catch (IOException exception){
            logger.error("There is no such file");
        }

        return productMap;
    }

    @Override
    public Map<String, DataFromDatabase> getDataFromDatabase(File file, FileSettings settings) throws FileNotFoundException {
            return getDataFromReader(new FileReader(file),settings);
    }

    public Map<String,Integer> reverseMap(Map<Integer,String> map){
        Map<String, Integer> reverseMap = new HashMap<>();
        for (Map.Entry<Integer,String> entry: map.entrySet()) {
            reverseMap.put(entry.getValue(), entry.getKey());
        }
        return reverseMap;
    }
}
