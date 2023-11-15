package com.zygadlo.ordermanagementsystem.repository;

import com.zygadlo.ordermanagementsystem.model.Savings;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SavingsRepository extends MongoRepository<Savings,String> {
    Optional<Savings> findByMonthOfSavings(String month);
}
