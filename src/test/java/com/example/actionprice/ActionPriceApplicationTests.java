package com.example.actionprice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.example.actionprice.originalAuctionData.AuctionDataEntity;
import com.example.actionprice.originalAuctionData.AuctionDataRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.example.actionprice.originalAuctionData.AuctionDataFetcher;
import com.example.actionprice.originalAuctionData.apiRequestObj.AuctionDataBody;
import com.example.actionprice.originalAuctionData.apiRequestObj.AuctionDataRow;
import com.example.actionprice.sendEmail.SendEmailComponent;

import reactor.core.publisher.Flux;

// TODO 데이터 저장 기능은 논의 후 구현
@SpringBootTest(classes = {ActionPriceApplication.class})
class ActionPriceApplicationTests {

	@Autowired
	AuctionDataFetcher auctionDataFetcher;
	
	@Autowired
	SendEmailComponent sendEmailComponent;

	@Autowired
	AuctionDataRepository auctionDataRepository;

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
	void auctionDataObjectTest() throws Exception {
		AuctionDataBody responseEntity = auctionDataFetcher.getOriginalAuctionData_AuctionDataBody("20150801", "서울강서도매시장");
		List<AuctionDataRow> list =  responseEntity.getContent().getRow();
				
		list.stream().forEach(System.out::print);
	}
	
	/**
	 * @author 연상훈
	 * @created 24/10/01 20:58
	 * @updated 24/10/01 20:58
	 * @info AuctionData 객체와 flux를 사용한 api 연결 테스트 성공.
	 */

	@Test
	void auctionDataFluxTest() throws Exception {
		Flux<AuctionDataRow> auctionDataFlux = auctionDataFetcher.getOriginalAuctionData_Flux("20150801", "서울강서도매시장");

		auctionDataFlux.toStream().forEach(System.out::print);
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
		sendEmailComponent.sendSimpleMail("dnqnp@naver.com", "스프링부트테스트", "테스트");
	}

	/**
	 * @author 연상훈
	 * @created 24/10/01 23:13
	 * @updated 24/10/01 23:13
	 * @info 복합이메일 전송 테스트 성공
	 */
	@Test
	@Disabled
	void sendMimeEmailTest() throws Exception {
		sendEmailComponent.sendMimeMail("dnqnp@naver.com", "스프링부트테스트", "테스트");
	}

	@Test
	void createAuctionDataEntity() throws Exception {
		Flux<AuctionDataRow> auctionDataFlux = auctionDataFetcher.getOriginalAuctionData_Flux("20150801", "서울강서도매시장");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");



		auctionDataFlux.toStream().map(row -> {
			AuctionDataEntity auctionDataEntity = AuctionDataEntity.builder()
					.delngDe(LocalDate.parse(row.getDelngDe(), formatter))
					.whsalMrktNewCode(row.getWhsalMrktNewCode())
					.whsalMrktNewNm(row.getWhsalMrktNewNm())
					.whsalMrktCode(row.getWhsalMrktCode())
					.whsalMrktNm(row.getWhsalMrktNm())
					.catgoryNewCode(row.getCatgoryNewCode())
					.catgoryNewNm(row.getCatgoryNewNm())
					.catgoryCode(row.getCatgoryCode())
					.catgoryNm(row.getCatgoryNm())
					.stdPrdlstNewCode(row.getStdPrdlstNewCode())
					.stdPrdlstNewNm(row.getStdPrdlstNewNm())
					.stdPrdlstCode(row.getStdPrdlstCode())
					.stdPrdlstNm(row.getStdPrdlstNm())
					.stdSpciesNewCode(row.getStdSpciesNewCode())
					.stdSpciesNewNm(row.getStdSpciesNewNm())
					.stdSpciesCode(row.getStdSpciesCode())
					.stdSpciesNm(row.getStdSpciesNm())
					.delngPrut(row.getDelngPrut())
					.stdUnitNewCode(row.getStdUnitNewCode())
					.stdUnitNewNm(row.getStdUnitNewNm())
					.sbidPric(row.getSbidPric())
					.stdMtcNewCode(row.getStdMtcNewCode())
					.stdMtcNewNm(row.getStdMtcNewNm())
					.cprMtcCode(row.getCprMtcCode())
					.cprMtcNm(row.getCprMtcNm())
					.delngQy(row.getDelngQy())
					.build();

			return auctionDataEntity;
		}).forEach(entity -> {auctionDataRepository.save(entity);});
	}

}
