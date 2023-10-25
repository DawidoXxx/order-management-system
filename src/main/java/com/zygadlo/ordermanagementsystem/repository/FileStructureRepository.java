package com.zygadlo.ordermanagementsystem.repository;

import com.zygadlo.ordermanagementsystem.model.FileSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileStructureRepository extends MongoRepository<FileSettings,String> {

    FileSettings findByFileName(String name);
}
