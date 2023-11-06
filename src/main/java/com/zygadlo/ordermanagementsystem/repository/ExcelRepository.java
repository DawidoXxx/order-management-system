package com.zygadlo.ordermanagementsystem.repository;

import com.zygadlo.ordermanagementsystem.model.DataFromDatabase;
import com.zygadlo.ordermanagementsystem.model.FileSettings;
import com.zygadlo.ordermanagementsystem.model.Product;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Repository
public class ExcelRepository implements DataRepository{

    @Override
    public Map<String, DataFromDatabase> getDataFromDatabase(File file, FileSettings settings) {

        Map<String, DataFromDatabase> products = new HashMap<>();
        Map<String,Integer> fieldOrder;
        fieldOrder = reverseMap(settings.getFieldsOrderMap());

        try {

            FileInputStream fis = new FileInputStream(file);

            //CREATE EXCEL OBJECT
            XSSFWorkbook workbook = new XSSFWorkbook(fis);

            //GET FIRST SHEET
            XSSFSheet sheet = workbook.getSheetAt(0);

            //Wskaźnik na rząd
            Iterator<Row> rowIterator = sheet.iterator();

            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (row.getCell(5) == null)
                    continue;
                if (row.getCell(5).getCellType() == Cell.CELL_TYPE_BLANK)
                    continue;

                if (row.getCell(5).getStringCellValue().equals("Jm."))
                    continue;

                if (row.getCell(7).getCellType() != Cell.CELL_TYPE_NUMERIC)
                    continue;

                String kodEan = row.getCell(fieldOrder.get(FileSettings.Fields.EAN.name())).getStringCellValue();

                String kodShop = row.getCell(fieldOrder.get(FileSettings.Fields.SELLERCODE.name())).getStringCellValue();
                String nameOf = row.getCell(fieldOrder.get(FileSettings.Fields.NAME.name())).getStringCellValue();

                double cena = row.getCell(fieldOrder.get(FileSettings.Fields.PRICE.name())).getNumericCellValue();

                products.put(kodEan, new DataFromDatabase(cena, kodShop, nameOf));

            }
        } catch (IOException ioException){
            System.out.println(ioException.getMessage());
        }

        return products;
    }

    private Map<String,Integer> reverseMap(Map<Integer,String> map){
        Map<String, Integer> reverseMap = new HashMap<>();
        for (Map.Entry<Integer,String> entry: map.entrySet()) {
            reverseMap.put(entry.getValue(), entry.getKey());
        }
        return reverseMap;
    }
}
