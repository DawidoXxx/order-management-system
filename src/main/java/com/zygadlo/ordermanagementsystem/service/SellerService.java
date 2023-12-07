package com.zygadlo.ordermanagementsystem.service;

import com.zygadlo.ordermanagementsystem.model.Seller;
import com.zygadlo.ordermanagementsystem.repository.SellerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    public Seller findSellerByName(String name){
        return sellerRepository.findByName(name);
    }

    public void saveSeller(Seller seller) {
        sellerRepository.save(seller);
    }

    public List<Seller> findAllSellers() {
        return sellerRepository.findAll();
    }
}
