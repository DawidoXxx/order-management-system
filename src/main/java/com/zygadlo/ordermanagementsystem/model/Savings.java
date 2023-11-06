package com.zygadlo.ordermanagementsystem.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@Document("Savings")
public class Savings {

    @Id
    private String id;
    private String monthOfSavings;
    private double savedCash;
    private String timeOfSavings;

    public Savings(String monthOfSavings, double savedCash, String timeOfSavings) {
        this.monthOfSavings = monthOfSavings;
        this.savedCash = savedCash;
        this.timeOfSavings = timeOfSavings;
    }

    public Savings(String monthOfSavings, double savedCash) {
        this.monthOfSavings = monthOfSavings;
        this.savedCash = savedCash;
    }

    public void addCash(double cashSavedThisTime){
        savedCash = savedCash + cashSavedThisTime;
    }

    public String getMonth(){
        String month;
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        month = formatter.format(currentDate);
        return month;
    }

}
