package com.zygadlo.ordermanagementsystem.repository;

import com.zygadlo.ordermanagementsystem.model.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class ProductsRepositoryTest {

    @Autowired
    ProductsRepository testedRepository;

    @AfterEach
    void tearDown() {
        testedRepository.deleteAll();
    }

    @Test
    void findProductByEan() {
        //given
        String ean = "590333424432";
        Product product = new Product(ean,"Płatki owsiane Lubella",1);
        testedRepository.save(product);

        //when
        Product resultProduct = testedRepository.findByEan(ean);

        //then
        assertEquals(product.getName(),resultProduct.getName());
        assertThat(product.getName()).isEqualTo(resultProduct.getName());
    }

    @Test
    void existsByEan() {
        //given
        String ean = "590333424432";
        String notExistingEan = "873654593535";
        Product product = new Product(ean,"Płatki owsiane Lubella",1);
        testedRepository.save(product);

        //when
        boolean result = testedRepository.existsByEan(ean);
        boolean resultWithNotExistingEan = testedRepository.existsByEan(notExistingEan);

        //then
        assertThat(result).isTrue();
        assertThat(resultWithNotExistingEan).isFalse();
    }

    @Test
    void findBySearchNamesRegex() {
    }

    @Test
    void getAllProductsByName() {

    }

    @Test
    void getAllProductsByEan() {

    }
}