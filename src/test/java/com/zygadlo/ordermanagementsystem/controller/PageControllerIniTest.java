package com.zygadlo.ordermanagementsystem.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PageController.class)
class PageControllerIniTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getMainPage() {

    }

    @Test
    void updateProducts() {
    }

    @Test
    void uploadDatabaseFile() {
    }

    @Test
    void uploadOrderFile() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/uploadOrderFile");
        MvcResult result = mockMvc.perform(request).andReturn();
    }

    @Test
    void createOrder() {
    }

    @Test
    void deleteDataBase() {
    }

    @Test
    void noShopSite() {
    }

    @Test
    void findProductByEanOrName() {
    }

    @Test
    void zipDownload() {
    }
}