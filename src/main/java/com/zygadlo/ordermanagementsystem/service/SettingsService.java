package com.zygadlo.ordermanagementsystem.service;

import com.zygadlo.ordermanagementsystem.model.FileSettings;
import com.zygadlo.ordermanagementsystem.repository.FileStructureRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SettingsService {

    FileStructureRepository fileStructureRepo;

    public SettingsService(FileStructureRepository fileStructureRepo) {
        this.fileStructureRepo = fileStructureRepo;
    }

    public void saveSettings(FileSettings fileSettings){
        fileStructureRepo.save(fileSettings);
    }

    public FileSettings findByNameOfFile(String whatFile) {
        return fileStructureRepo.findByFileName(whatFile);
    }

    public Map<Integer, String> createFieldsOrderMap(Integer eanOrder, Integer nameOrder, Integer amountOrder,
                                                     Integer sellerCodeOrder, Integer priceOrder, Integer none) {
        HashMap<Integer,String> mapOfFieldsOrder = new HashMap<>();

        mapOfFieldsOrder.put(eanOrder, FileSettings.Fields.EAN.name());
        mapOfFieldsOrder.put(nameOrder, FileSettings.Fields.NAME.name());
        mapOfFieldsOrder.put(amountOrder, FileSettings.Fields.AMOUNT.name());
        mapOfFieldsOrder.put(sellerCodeOrder, FileSettings.Fields.SELLERCODE.name());
        mapOfFieldsOrder.put(priceOrder, FileSettings.Fields.PRICE.name());
        mapOfFieldsOrder.put(none, FileSettings.Fields.EMPTY.name());

        return mapOfFieldsOrder;
    }

    public void saveOrUpdateFileSettings(String whatFile, String extension, String separator,
                                         Map<Integer, String> fieldsOrder) {
        FileSettings fileSettings;

            if (findByNameOfFile(whatFile) != null) {
                fileSettings = findByNameOfFile(whatFile);
                fileSettings.setExtension(extension);
                fileSettings.setSeparator(separator);
                fileSettings.setFieldsOrderMap(fieldsOrder);
            } else
                fileSettings = new FileSettings(whatFile, separator, extension, fieldsOrder);

            saveSettings(fileSettings);
    }

    //if its database file than we need 4 values in map
    //if not we need to check if its in series user can mistake and put for example 0,1,2,8
    public boolean checkIfValidFieldsOrder(Map<Integer,String>map,String nameOfFile) {
        if (nameOfFile.contains("DB")&&map.size()==4)
            return true;

        return !nameOfFile.contains("DB") && checkIfInASeries(map);
    }

    //check if there isnt blank column between fields
    //ean=0 name=2 amount=3 price=4 => return false
    //if we want to have blank space between column we have empty field
    private boolean checkIfInASeries(Map<Integer,String>map){
        for (int i=0;i<map.size();i++){
            if (!map.containsKey(i))
                return false;
        }
        return true;
    }
}
