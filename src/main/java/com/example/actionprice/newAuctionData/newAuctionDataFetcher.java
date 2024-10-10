package com.example.actionprice.newAuctionData;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

@Data
@Component
public class newAuctionDataFetcher
{
    @Value("${newAuctionData.url}")
    String basenewAuctionUrl;

    @Value("${newAuctionData.encodedKey}")
    String newauctionEncodedKey;

    private final WebClient webClient;

    public newAuctionDataFetcher() {
        this.webClient = WebClient.builder().build();
    }


    public ResponseEntity<String> getNewAuctionData_String(String date) throws Exception {

        URI uri = composeUri(date);

        ResponseEntity<String> responseEntity = webClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class)
                .block();

        return responseEntity;
    }





    private URI composeUri(String date) throws UnsupportedEncodingException, URISyntaxException {
        String apiType = "json"; // json
        String saleDate = date; // 기록을 검색할 날짜


        String url = String.format(
                "%s?newauctionEncodedKey=%s&apiType=%s&&saleDate=%s",
                basenewAuctionUrl,
                newauctionEncodedKey,
                apiType,
                saleDate
        );
        // format 구성하면서 검색 조건을 뒤에 추가하면 됨

        return new URI(url);

}
}
