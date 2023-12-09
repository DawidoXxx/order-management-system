package com.zygadlo.ordermanagementsystem.repository;

import com.zygadlo.ordermanagementsystem.model.DataFromDatabase;
import com.zygadlo.ordermanagementsystem.model.FileSettings;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CsvRepositoryTest {

    @Test
    void getDataFromReader() {
        FileSettings fileSettings = new FileSettings("CsvFileStructure",";",".csv",createFieldOrderMap());
        CsvRepository csvRepository = new CsvRepository();

        // Utwórz przykładową zawartość pliku CSV
        String csvContent = "EAN;SELLERCODE;NAME;PRICE\n" +
                "123456;S123;ProductA;10.5\n" +
                "789012;S456;ProductB;15.0";

        Map<String,DataFromDatabase> result = csvRepository.getDataFromReader(new StringReader(csvContent),fileSettings);


            // Assert
            assertEquals(2, result.size());

            assertEquals(10.5, result.get("123456").getPrice());
            assertEquals("S123", result.get("123456").getSallersCode());
            assertEquals("ProductA", result.get("123456").getNameOfProduct());

            assertEquals(15.0, result.get("789012").getPrice());
            assertEquals("S456", result.get("789012").getSallersCode());
            assertEquals("ProductB", result.get("789012").getNameOfProduct());

        }

        private Map<Integer,String> createFieldOrderMap() {
        Map<Integer,String> fieldOrderMap = new HashMap<>();
        fieldOrderMap.put(0,"EAN");
        fieldOrderMap.put(1,"SELLERCODE");
        fieldOrderMap.put(2,"NAME");
        fieldOrderMap.put(3,"PRICE");
        return fieldOrderMap;
    }
}