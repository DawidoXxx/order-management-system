package com.zygadlo.ordermanagementsystem.service;

import com.zygadlo.ordermanagementsystem.model.Shop;
import com.zygadlo.ordermanagementsystem.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopService {

    private ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public boolean checkIsThereAreShopsInDatabase(){
        return shopRepository.findAll().size()>0;
    }

    public List<Shop> findAllShops() {
        return shopRepository.findAll();
    }
}
