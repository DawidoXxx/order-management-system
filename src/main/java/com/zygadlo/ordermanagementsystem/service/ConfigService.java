package com.zygadlo.ordermanagementsystem.service;

import com.zygadlo.ordermanagementsystem.model.Shop;
import com.zygadlo.ordermanagementsystem.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ConfigService {

    private ShopRepository shopRepository;

    public ConfigService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public boolean checkIfSellerConfigFileExist(String filename) {
        File file = new File(filename);
        return file.exists();
    }

    public void saveSellersToMongoDB(String fileName) {
        shopRepository.deleteAll();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            List<Shop> shops = stream.map(Shop::new).collect(Collectors.toList());
            shopRepository.saveAll(shops);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
