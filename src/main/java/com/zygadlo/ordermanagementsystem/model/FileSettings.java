package com.zygadlo.ordermanagementsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@Document(collection = "fileSettings")
public class FileSettings {

    public enum Fields{
        EAN,
        NAME,
        PRICE,
        AMOUNT,
        SELLERCODE,
        EMPTY
    }

    @Id
    private String id;
    private String fileName;
    private String separator;
    private String extension;
    private Map<Integer,String> fieldsOrderMap;

    public FileSettings(){
        fieldsOrderMap = new HashMap<>();
    }

    public FileSettings(String fileName, String separator, String extension, Map<Integer, String> fieldsOrderMap) {
        this.fileName = fileName;
        this.separator = separator;
        this.extension = extension;
        this.fieldsOrderMap = fieldsOrderMap;
    }
}
