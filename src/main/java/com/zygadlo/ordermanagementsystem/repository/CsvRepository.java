package com.zygadlo.ordermanagementsystem.repository;

import com.zygadlo.ordermanagementsystem.model.DataFromDatabase;
import com.zygadlo.ordermanagementsystem.model.FileSettings;
import com.zygadlo.ordermanagementsystem.model.Product;
import org.springframework.aot.generate.InMemoryGeneratedFiles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class CsvRepository implements DataRepository{

    @Value("${upload.database}")
    private String databasePath;

    @Override
    public Map<String, DataFromDatabase> getDataFromDatabase(File file, FileSettings settings) {

        Map<String,DataFromDatabase> productMap = new HashMap<>();
        Map<String,Integer> fieldOrder = reverseMap(settings.getFieldsOrderMap());

        BufferedReader bufferedReader;
        String readedLine;
        String[] readedFields;

        try{
            bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()));

            while((readedLine = bufferedReader.readLine())!=null){
                readedFields = readedLine.split(settings.getSeparator());

                if (readedFields[0].equals("EAN")||readedFields[0].equals("INDEKS"))
                    continue;

                String priceString = readedFields[fieldOrder.get(FileSettings.Fields.PRICE.name())];
                Double price = Double.parseDouble(priceString);

                productMap.put(readedFields[fieldOrder.get(FileSettings.Fields.EAN.name())],
                        new DataFromDatabase(price,readedFields[fieldOrder.get(FileSettings.Fields.SELLERCODE.name())],
                                readedFields[fieldOrder.get(FileSettings.Fields.NAME.name())]));
            }

        }catch (IOException exception){

        }

        return productMap;
    }

    private Map<String,Integer> reverseMap(Map<Integer,String> map){
        Map<String, Integer> reverseMap = new HashMap<>();
        for (Map.Entry<Integer,String> entry: map.entrySet()) {
            reverseMap.put(entry.getValue(), entry.getKey());
        }
        return reverseMap;
    }
}
