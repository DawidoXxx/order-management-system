package com.zygadlo.ordermanagementsystem.repository;

import com.zygadlo.ordermanagementsystem.model.DataFromDatabase;
import com.zygadlo.ordermanagementsystem.model.FileSettings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExcelRepositoryTest {

    @Test
    void getDataFromWorkBook() {
        XSSFWorkbook workbook = createWorkBookForTest();
        ExcelRepository excelRepository = new ExcelRepository();
        Map<Integer, String> orderMap = createFieldOrderMap();
        FileSettings fileSettings = new FileSettings("ExcelDB", ";", ".xlsx", orderMap);
        Map<String, DataFromDatabase> result = excelRepository.getDataFromWorkBook(workbook, fileSettings);

        assertEquals(5,result.size());
        assertEquals(4.2,result.get("591113913212355").getPrice());
    }

    private XSSFWorkbook createWorkBookForTest() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Limonex products");
        Object[][] datatypes = {
                {"EAN", "SELLERCODE", "CENA_NETTO", "CENA_BRUTTO", "NAZWA", "Jm."},
                {"593939392323932", "PJ1212", 2.4, 3.0, "Jajka 10szt", "10"},
                {"591113913212355", "PS3321", 4.2, 5.4, "Ser biały 0,5kg", "1"},
                {"596774335542111", "PW3112", 1.3, 1.8, "Wafelek Prince Polo", "4"},
                {"595453345515799", "PN3155", 0.8, 1.2, "Woda mineralna Zywiec Zdrój", "1"},
                {"592226592756331", "PM6565", 2.3, 2.9, "Kasza manna", "2"}
        };

        int rowNum = 0;

        for (Object[] datatype : datatypes) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (Object field : datatype) {
                Cell cell = row.createCell(colNum++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                } else if (field instanceof Double) {
                    cell.setCellValue((Double) field);
                }
            }
        }
        return workbook;
    }

    private Map<Integer, String> createFieldOrderMap() {
        Map<Integer, String> fieldOrderMap = new HashMap<>();
        fieldOrderMap.put(0, "EAN");
        fieldOrderMap.put(1, "SELLERCODE");
        fieldOrderMap.put(2, "PRICE");
        fieldOrderMap.put(3, "PRICEBRUTTO");
        fieldOrderMap.put(4, "NAME");
        return fieldOrderMap;
    }
}