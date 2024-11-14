package com.example.actionprice.auctionData;

import com.example.actionprice.auctionData.detailCategory.AllSortingComponent;
import com.example.actionprice.auctionData.service.AuctionEntityService;
import com.example.actionprice.auctionData.originAuctionData.OriginAuctionDataFetcher;
import com.example.actionprice.auctionData.originAuctionData.originApiRequestObj.OriginAuctionDataRow;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@Log4j2
public class OriginAuctionDataTests {

    @Autowired
    private OriginAuctionDataFetcher originAuctionDataFetcher;

    @Autowired
    private AllSortingComponent allSortingComponent;

    @Autowired
    private AuctionEntityService auctionEntityService;

    @Test
    @Disabled
    void auctionDataPrintTest() throws Exception {
        String reponseBody = originAuctionDataFetcher.getAuctionData_String("1101", "2024-10-20", "100");
        System.out.println(reponseBody);
    }
    
    /**
     * @author 연상훈
     * @created 2024-11-09 오후 11:19
     * @updated 2024-11-14 오전 10:56 [연상훈] : 데이터 저장 완료
     */
    @Test
    void auctionDataFluxTest() throws Exception {
        String year = "2024";
        String month = "11";
        int startDay = 10;
        int endDay = 14;

        for (int i = startDay; i <= endDay; i++) {
            // 날짜 형식 맞추기
            String day = String.format("%02d", i);
            String date = String.format("%s-%s-%s", year, month, day);
            log.info("date : " + date);

            allSortingComponent.getMarket_code_map().entrySet().stream().forEach(marketCodeEntry -> {
                // key : 지역 코드, value : 지역 이름
                log.info("marketCode : {} | marketName : {}", marketCodeEntry.getKey(), marketCodeEntry.getValue());

                allSortingComponent.getGrand_sort().entrySet().stream().forEach(grandSortEntry -> {
                    // key : 대분류 코드, value : 대분류 이름
                    log.info("grandSortCode : {} | grandSortName : {}", grandSortEntry.getKey(), grandSortEntry.getValue());

                    CountDownLatch latch = new CountDownLatch(1); // 이 객체는 비동기 작업이 완료될 때까지 메인 스레드를 차단

                    try {
                        log.info("flux");
                        Flux<OriginAuctionDataRow> flux = originAuctionDataFetcher.getAuctionData_Flux(marketCodeEntry.getKey(), date, grandSortEntry.getKey());
                        flux.subscribe(row -> {
                            log.info("flux - row");
                            log.info("row : " + row.toString());
                            auctionEntityService.saveEntityByCategory(row, date, marketCodeEntry.getValue(), grandSortEntry.getValue());
                        }, error -> {
                            log.error("Error retrieving auction data: {}", error.getMessage());
                            latch.countDown(); // 에러 발생 시 대기중인 스레드 헤제
                        }, latch::countDown); // 완료 시 대기중인 스레드 헤제

                        latch.await(); // 비동기 작업이 완료될 때까지 메인 스레드를 대기
                    } catch (Exception e) {
                        log.error("Exception occurred: {}", e.getMessage());
                        throw new RuntimeException(e);
                    }
                });
            });
        }
    }


}
