package com.example.actionprice.newAuctionData;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
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


    /**
     * @author homin
     * @created 2024. 10. 10. 오후 3:53
     * @updated 2024. 10. 10. 오후 3:53
     * @info WebClient 용랑 늘리기
     */
    public WebClient createWebClient() {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))  // 16MB로 설정
                .build();

        return WebClient.builder()
                .exchangeStrategies(strategies)
                .baseUrl("https://at.agromarket.kr")
                .clientConnector(new ReactorClientHttpConnector())
                .build();
    }

    // 초기화
    public newAuctionDataFetcher() {
        this.webClient = createWebClient();
    }

    /**
     * @author homin
     * @created 2024. 10. 10. 오후 3:54
     * @updated 2024. 10. 10. 오후 3:54
     * @info API 호출 및 응답 처리 메서드
     */
    public void fetchAuctionData() {
        webClient.get()
                .uri("/openApi/price/originSale.do")  // API 엔드포인트 설정
                .retrieve()
                .bodyToMono(String.class)  // 응답을 문자열로 변환
                .doOnError(e -> System.out.println("Error occurred: " + e.getMessage()))  // 에러 처리
                .subscribe(response -> {
                    // 응답 데이터를 처리하는 부분
                    System.out.println("Response: " + response);
                });
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
                "%s?serviceKey=%s&apiType=%s&saleDate=%s",
                basenewAuctionUrl,
                newauctionEncodedKey,
                apiType,
                saleDate
        );
        // format 구성하면서 검색 조건을 뒤에 추가하면 됨

        return new URI(url);

}
}
