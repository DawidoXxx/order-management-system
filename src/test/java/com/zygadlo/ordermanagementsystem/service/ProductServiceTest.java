package com.zygadlo.ordermanagementsystem.service;

import com.zygadlo.ordermanagementsystem.exception.NoFileStructureFoundException;
import com.zygadlo.ordermanagementsystem.exception.StorageException;
import com.zygadlo.ordermanagementsystem.model.DataFromDatabase;
import com.zygadlo.ordermanagementsystem.model.FileSettings;
import com.zygadlo.ordermanagementsystem.repository.*;
import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest
class ProductServiceTest {

    @Mock
    private FileStructureRepository fileStructureRepository;
    @Mock
    private CsvRepository csvRepository;
    @Mock
    private ExcelRepository excelRepository;
    @Mock
    private ProductsRepository productRepository;
    @Mock
    private SellerRepository sellerRepository;
    @Mock
    private DataBaseUpdateInfo dataBaseUpdateInfo;

    @Spy
    @InjectMocks
    private ProductService testedService;

    @Test
    public void testReadProductsFromDatabaseFilesExcel() throws StorageException, NoFileStructureFoundException, FileNotFoundException {
        File database = mock(File.class);
        FileSettings fileSettings = mock(FileSettings.class);
        when(database.getName()).thenReturn("example.xlsx");
        when(excelRepository.getDataFromDatabase(eq(database), any())).thenReturn(new HashMap<>());

        Map<String, DataFromDatabase> result = testedService.readProductsFromDatabaseFiles(database, fileSettings);

        verify(excelRepository, times(1)).getDataFromDatabase(any(File.class), any(FileSettings.class));
    }

    @Test
    public void testReadProductsFromDatabaseFilesCsv() throws StorageException, NoFileStructureFoundException, FileNotFoundException {
        File database = mock(File.class);
        FileSettings fileSettings = mock(FileSettings.class);
        when(database.getName()).thenReturn("example.csv");

        when(csvRepository.getDataFromDatabase(eq(database), any())).thenReturn(new HashMap<>());

        Map<String, DataFromDatabase> result = testedService.readProductsFromDatabaseFiles(database, fileSettings);

        verify(csvRepository, times(1)).getDataFromDatabase(any(File.class), any(FileSettings.class));
    }

    @Test
    public void testReadProductsFromDatabaseFilesThrowException()throws StorageException, NoFileStructureFoundException {
        File database = mock(File.class);
        FileSettings fileSettings = mock(FileSettings.class);
        when(database.getName()).thenReturn("example.txt");
        //File mockDatabaseFile = new File("mockDatabase.txt");  // Plik z nieobsługiwanym rozszerzeniem
        //FileSettings mockFileSettings = new FileSettings();  // Ustawienia pliku (może być mockowane)

        // Act + Assert
        assertThrows(StorageException.class, () ->
                testedService.readProductsFromDatabaseFiles(database, fileSettings));
    }

    @Test
    public void testReadProductsFromDatabaseFilesThrowNoFileStructureFoundException()throws StorageException, NoFileStructureFoundException {
        File database = mock(File.class);
        FileSettings fileSettings = null;
        when(database.getName()).thenReturn("example.txt");
        //File mockDatabaseFile = new File("mockDatabase.txt");  // Plik z nieobsługiwanym rozszerzeniem
        //FileSettings mockFileSettings = new FileSettings();  // Ustawienia pliku (może być mockowane)

        // Act + Assert
        assertThrows(NoFileStructureFoundException.class, () ->
                testedService.readProductsFromDatabaseFiles(database, fileSettings));
    }

    @Test
    public void testUpdateProducts() throws FileNotFoundException {
        testedService.setDatabaseFilesPath("order/");
        File[] databases = new File[]{new File("example.xlsx")};
        when(testedService.getDataBaseFiles()).thenReturn(databases);
        when(fileStructureRepository.findByFileName(any())).thenReturn(mock(FileSettings.class));
        when(excelRepository.getDataFromDatabase(any(), any())).thenReturn(new HashMap<>());

        testedService.updateProducts(LocalDateTime.now());

        verify(productRepository, times(1)).deleteAll();
        verify(dataBaseUpdateInfo, times(1)).save(any());
    }
}