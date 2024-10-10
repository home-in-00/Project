package com.example.actionprice;

import com.example.actionprice.sendEmail.Pop3Properties;
import jakarta.mail.MessagingException;
import jakarta.mail.Store;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.example.actionprice.originalAuctionData.AuctionDataEntity;
import com.example.actionprice.originalAuctionData.AuctionDataRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.example.actionprice.originalAuctionData.AuctionDataFetcher;
import com.example.actionprice.originalAuctionData.apiRequestObj.AuctionDataBody;
import com.example.actionprice.originalAuctionData.apiRequestObj.AuctionDataRow;
import com.example.actionprice.sendEmail.SendEmailServiceImpl;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import reactor.core.publisher.Flux;

import javax.xml.parsers.DocumentBuilderFactory;

// TODO 데이터 저장 기능은 논의 후 구현
@SpringBootTest(classes = {ActionPriceApplication.class})
@Log4j2
class ActionPriceApplicationTests {

	@Autowired
	AuctionDataFetcher auctionDataFetcher;
	
	@Autowired
	SendEmailServiceImpl sendEmailServiceImpl;

	@Autowired
	AuctionDataRepository auctionDataRepository;

	@Autowired
	Pop3Properties pop3Properties;

	@Test
	@Disabled
	void sessionClose() throws Exception {
		try {
			Store store = pop3Properties.getPop3Store();
			if (store.isConnected()) {
				store.close(); // Store 연결 닫기
				log.info("POP3 Store connection closed.");
			}
		} catch (MessagingException e) {
			log.error("Error closing the POP3 Store: {}", e.getMessage());
		}
	}
	// TODO 비즈니스 로직에 대한 논의 후 해당 클래스에 대한 재조정이 필요함
	/**
	 * @author 연상훈
	 * @created 24/10/01 17:50
	 * @updated 24/10/01 17:50
	 * @info api 연결 테스트 성공.
	 */
	@Disabled
	@Test
	void auctionDataPrintTest() throws Exception {
		ResponseEntity<String> responseEntity = auctionDataFetcher.getOriginalAuctionData_String("20150801", "서울강서도매시장");
	 
		System.out.println(responseEntity.toString());	  	  
	}
	
	/**
	 * @author 연상훈
	 * @created 24/10/01 20:20
	 * @updated 24/10/01 20:20
	 * @info AuctionData 객체를 사용한 api 연결 테스트 성공.
	 */

	@Test
	@Disabled
	void auctionDataObjectTest() throws Exception {
		AuctionDataBody responseEntity = auctionDataFetcher.getOriginalAuctionData_AuctionDataBody("20150801", "서울강서도매시장");
		List<AuctionDataRow> list =  responseEntity.getContent().getRow();
				
		list.stream().forEach(System.out::print);
	}
	
	/**
	 * @author 연상훈
	 * @created 24/10/01 20:58
	 * @updated 24/10/01 20:58
	 * @info AuctionData 객체와 flux를 사용한 api 연결 테스트 성공. 텍스트 출력 테스트.
	 */
	@Test
	@Disabled
	void auctionDataFluxTest() throws Exception {

		String year = "2015";
		String month = "08";

		int endDay = 30;

		String[] marketNameArr = {"부산반여농산물도매시장"};

		for (int i=1; i<=endDay; i++){
			String day = String.valueOf(i);
			if (day.length() == 1){
				day = "0"+day;
			}

			String date = String.format("%s%s%s", year, month, day);

			for(String marketName : marketNameArr){
				Flux<AuctionDataRow> auctionDataFlux = auctionDataFetcher.getOriginalAuctionData_Flux(date, marketName);
				auctionDataFlux.toStream().forEach(System.out::print);
			}
		}

	}
	
	/**
	 * @author 연상훈
	 * @created 24/10/01 22:43
	 * @updated 24/10/01 22:43
	 * @info 심플이메일 전송 테스트 성공
	 */
	@Disabled
	@Test
	void sendSimpleEmailTest() throws Exception {
		sendEmailServiceImpl.sendSimpleMail("dnqnp@naver.com", "스프링부트테스트", "테스트");
	}

	/**
	 * @author 연상훈
	 * @created 24/10/01 23:13
	 * @updated 24/10/01 23:13
	 * @param : endDay = 각 달의 마지막 날이 언제인지 확인 후 입력
	 * @param : marketNameArr = 경매장 이름을 모두 여기에 입력
	 * @info 복합이메일 전송 테스트 성공
	 */
	@Test
	@Disabled
	void sendMimeEmailTest() throws Exception {
		sendEmailServiceImpl.sendMimeMail("dnqnp@naver.com", "스프링부트테스트", "테스트");
	}

	/**
	 * @author : 연상훈
	 * @created : 2024-10-06 오전 12:16
	 * @updated : 2024-10-06 오전 12:16
	 * @see :
	 * 1. 만약 농산/수산/축산 별로 테이블을 나누려면 여기에서 분류하는 로직이 필요함
	 * 2. 그리고 현재 만들어둔 AuctionDataEntity를 베이스엔티티로 두고, 다른 엔티티들에게 extends 하는 방식으로 가야 함
	 * 3. 거기에 service에서 각 엔티티별 repository를 두고, 팩토리 패턴으로 상황에 맞는 레포지토리가 나오게 해야 함.
	 * 4. 그 반대도 마찬가지
	 */
	@Test
	@Disabled
	void createAuctionDataEntity() throws Exception {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

		String year = "2015";
		String month = "08";

		int endDay = 30; // 각 달의 마지막 날이 언제인지 확인 후 입력

		String[] marketNameArr = {"부산반여농산물도매시장"}; // 경매장 이름을 모두 여기에 입력

		for (int i=1; i<=endDay; i++){
			String day = String.valueOf(i);
			if (day.length() == 1){
				day = "0"+day;
			}

			String date = String.format("%s%s%s", year, month, day);

			for(String marketName : marketNameArr){

				Flux<AuctionDataRow> auctionDataFlux = auctionDataFetcher.getOriginalAuctionData_Flux(date, marketName);

				auctionDataFlux.toStream().map(row -> {
					AuctionDataEntity auctionDataEntity = AuctionDataEntity.builder()
							.DELNG_DE(LocalDate.parse(row.getDELNG_DE(), formatter))
							.WHSAL_MRKT_NEW_CODE(row.getWHSAL_MRKT_NEW_CODE())
							.WHSAL_MRKT_NEW_NM(row.getWHSAL_MRKT_NEW_NM())
							.WHSAL_MRKT_CODE(row.getWHSAL_MRKT_CODE())
							.WHSAL_MRKT_NM(row.getWHSAL_MRKT_NM())
							.CATGORY_NEW_CODE(row.getCATGORY_NEW_CODE())
							.CATGORY_NEW_NM(row.getCATGORY_NEW_NM())
							.CATGORY_CODE(row.getCATGORY_CODE())
							.CATGORY_NM(row.getCATGORY_NM())
							.STD_PRDLST_NEW_CODE(row.getSTD_PRDLST_NEW_CODE())
							.STD_PRDLST_NEW_NM(row.getSTD_PRDLST_NEW_NM())
							.STD_PRDLST_CODE(row.getSTD_PRDLST_CODE())
							.STD_PRDLST_NM(row.getSTD_PRDLST_NM())
							.STD_SPCIES_NEW_CODE(row.getSTD_SPCIES_NEW_CODE())
							.STD_SPCIES_NEW_NM(row.getSTD_SPCIES_NEW_NM())
							.STD_SPCIES_CODE(row.getSTD_SPCIES_CODE())
							.STD_SPCIES_NM(row.getSTD_SPCIES_NM())
							.DELNG_PRUT(row.getDELNG_PRUT())
							.STD_UNIT_NEW_CODE(row.getSTD_UNIT_NEW_CODE())
							.STD_UNIT_NEW_NM(row.getSTD_UNIT_NEW_NM())
							.SBID_PRIC(row.getSBID_PRIC())
							.STD_MTC_NEW_CODE(row.getSTD_MTC_NEW_CODE())
							.STD_MTC_NEW_NM(row.getSTD_MTC_NEW_NM())
							.CPR_MTC_CODE(row.getCPR_MTC_CODE())
							.CPR_MTC_NM(row.getCPR_MTC_NM())
							.DELNG_QY(row.getDELNG_QY())
							.build();

					return auctionDataEntity;
				}).forEach(entity -> {auctionDataRepository.save(entity);});
			}
		}

	}

}
