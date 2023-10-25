package com.zygadlo.ordermanagementsystem.service;

import com.zygadlo.ordermanagementsystem.exception.StorageException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class StorageService {

    @Value("${upload.orderFile}")
    private String orderToComputePath;

    @Value("${upload.database}")
    private String databasesPath;

    @Value("${create.ordersToSend}")
    private String ordersToSendPath;

    //Check if exist
    private boolean createIfNotExist(String path){
        File fileToCheck = new File(path);
        if (!fileToCheck.exists()){
            return fileToCheck.mkdirs();
        }
        return false;
    }

    //Store order or database file to compute
    //isOrderFile -> indicate where we gonna save file
    //newFileName -> its new, more user friendly name of database or order with extension
    public void uploadFile(MultipartFile file,boolean isOrderFile,String newFileName){
        String path;
        if (isOrderFile) {
            path = orderToComputePath;
            createIfNotExist(path);
//            deleteFile(new File(path), file.getName());
            deleteFile(new File(path), newFileName);
        }
        else{
            path = databasesPath;
            createIfNotExist(path);
            deleteFile(new File(path), newFileName);
        }

        if (file.isEmpty())
            throw new StorageException("This file is empty");

        try{
            InputStream is = file.getInputStream();
            Files.copy(is, Paths.get(path+newFileName));
        }catch (IOException ex){
            String msg = String.format("Failed to store file: %s",file.getName());
            throw new StorageException(msg,ex);
        }
    }

    //delete existing database file
    private boolean deleteFile(@NonNull File directory,@NonNull String fileNameToDelete){
        File[] files = directory.listFiles();

        if (files==null||files.length==0)
            return false;

        for (File file:files) {
            if (file.getName().equals(fileNameToDelete))
                return file.delete();
        }
        return false;
    }

    //Save orders to send

}
