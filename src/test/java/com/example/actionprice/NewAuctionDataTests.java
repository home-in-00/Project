package com.example.actionprice;


import com.example.actionprice.newAuctionData.newAuctionDataFetcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class NewAuctionDataTests
{
    @Autowired
    newAuctionDataFetcher newAuctionDataFetcher;


    @Test
    void newAuctionDataPrintTest() throws Exception {
        ResponseEntity<String> responseEntity = newAuctionDataFetcher.getNewAuctionData_String("20241009");
        System.out.println(responseEntity.toString());
    }
}
