package com.example.actionprice.originalAuctionData;

import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.actionprice.originalAuctionData.apiRequestObj.AuctionDataBody;
import com.example.actionprice.originalAuctionData.apiRequestObj.AuctionDataRow;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

// TODO 이것을 활용한 비즈니스에 로직에 대한 논의 필요. 그리고 예외 처리를 매우 대충 해뒀음. 에러 대응 로직의 구체화 필요
// TODO 이게 날짜별로 데이터 호출이라 확실히 데이터를 우리가 관리할 필요가 있겠음
/**
* @author 연상훈
* @created 24/10/01 17:50
* @updated 24/10/01 17:50
* @value : baseAuctionUrl = api를 사용하기 위한 기본 경로. 해당 값을 application.properties에서 불러옴
* @value : auctionEncodedKey = api를 사용하기 위한 기본 키(인코딩된 상태). 해당 값을 application.properties에서 불러옴
* @value : WebClient = 중복되는 객체 생성을 피하기 위해
* @info api의 간편한 재사용을 위해 AuctionDataFetcher 클래스를 만들어서 @Component 로 등록하여 중복되는 객체 생성을 피하고, 메모리 사용을 줄임. 그리고 내장된 메서드를 통해 값을 반환함.
*/
@Component
public class AuctionDataFetcher {
	
	@Value("${auctionData.url}")
	String baseAuctionUrl;
	
	@Value("${auctionData.encodedKey}")
	String auctionEncodedKey;
	
	private final WebClient webClient;

	public AuctionDataFetcher() {
		this.webClient = WebClient.builder().build();
	}

	/**
	 * @author 연상훈
	 * @created 24/10/01 19:04
	 * @updated 24/10/01 19:04
	 * @param : date = 가져올 데이터의 거래일시(composeUri에 사용할 매개변수)
	 * @param : marketName = 가져올 데이터의 거래 장소(경매장 이름)(composeUri에 사용할 매개변수)
	 * @info 테스트를 위해 간단하게 String 타입으로 우선 구현
	 * @throws Exception
	 */
	public ResponseEntity<String> getOriginalAuctionData_String(String date, String marketName) throws Exception {
		
		URI uri = composeUri(date, marketName);

	    ResponseEntity<String> responseEntity = webClient.get()
	            .uri(uri)
	            .accept(MediaType.APPLICATION_JSON)
	            .retrieve()
	            .toEntity(String.class)
	            .block();
	    
	    return responseEntity;
	}
	
	/**
	 * @author 연상훈
	 * @created 24/10/01 19:04
	 * @updated 24/10/01 19:04
	 * @param : date = 가져올 데이터의 거래일시(composeUri에 사용할 매개변수)
	 * @param : marketName = 가져올 데이터의 거래 장소(경매장 이름)(composeUri에 사용할 매개변수)
	 * @throws Exception
	 * @info api의 반환 데이터 구조에 맞춘 OriginAuctionDataBody 객체를 사용하여 비즈니스에 로직에 활용할 수 있도록 구성.
	 * 그리고 웹 클라이언트를 사용함으로써 비동기적인 로직 수행 구현. 
	 */
	public AuctionDataBody getOriginalAuctionData_AuctionDataBody(String date, String marketName) throws Exception {

	    URI uri = composeUri(date, marketName);

	    // 요청을 보내고 응답 받기
	    AuctionDataBody auctionDataBody = webClient.get()
	            .uri(uri)
	            .accept(MediaType.APPLICATION_JSON)
	            .retrieve()
	            .bodyToMono(AuctionDataBody.class)
//	            .onErrorResume(e -> {                  
//              return Mono.empty();
//          }) 에러에 대한 대응 로직
	            .block();
	    
	    return auctionDataBody;
	}
	
	/**
	 * @author 연상훈
	 * @created 24/10/01 19:04
	 * @updated 24/10/01 19:04
	 * @param : date = 가져올 데이터의 거래일시(composeUri에 사용할 매개변수)
	 * @param : marketName = 가져올 데이터의 거래 장소(경매장 이름)(composeUri에 사용할 매개변수)
	 * @throws Exception
	 * @info api의 반환 데이터 구조에 맞춘 OriginAuctionDataBody 객체를 사용하여 비즈니스에 로직에 활용할 수 있도록 구성. 그리고 Flux를 사용함으로써 웹클라이언트보다도 빠른 비동기적인 로직 수행 구현.
	 */
	public Flux<AuctionDataRow> getOriginalAuctionData_Flux(String date, String marketName) throws Exception {

	    URI uri = composeUri(date, marketName);

	    return webClient.get()
	            .uri(uri)
	            .accept(MediaType.APPLICATION_JSON)
	            .retrieve()
	            .bodyToMono(AuctionDataBody.class)
//	            .onErrorResume(e -> {                  
//                    return Mono.empty();
//                }) 에러에 대한 대응 로직
	            .flatMapMany(body -> Flux.fromIterable(body.getContent().getRow()));
	}
	
	/**
	 * @author 연상훈
	 * @created 24/10/01 20:26
	 * @updated 24/10/01 20:26
	 * @param date = 가져올 데이터의 거래일시
	 * @param marketName = 가져올 데이터의 거래 장소(경매장 이름)
	 * @throws UnsupportedEncodingException, URISyntaxException
	 * @info uri 구성을 위한 로직이 반복되니까 메서드로 구현하여 간략하게 함. 아래는 공식제공 샘플 output. WHSAL_MRKT_NM= 의 값을 보면 인코딩한 것이 맞음
	 * @see {http://211.237.50.150:7080/openapi/sample/xml/Grid_20151127000000000313_1/1/5?DELNG_DE=20150801&WHSAL_MRKT_NM=%EC%84%9C%EC%9A%B8%EA%B0%95%EC%84%9C%EB%8F%84%EB%A7%A4%EC%8B%9C%EC%9E%A5}
	 */
	private URI composeUri(String date, String marketName) throws UnsupportedEncodingException, URISyntaxException {
		String TYPE = "json"; // json
	    int START_INDEX = 1; // 설명이 따로 없음. 받아올 페이지 번호인 듯?
	    int END_INDEX = 5; // 설명이 따로 없음. 받아올 페이지 번호인 듯?
	    String DELNG_DE = date; // 기록을 검색할 날짜
	    String WHSAL_MRKT_NM = marketName; // 어느 도매장에서의 기록인지 검색
	    
	    String url = String.format(
                "%s/%s/%s/Grid_20151127000000000311_1/%s/%s?DELNG_DE=%s&WHSAL_MRKT_NM=%s",
                baseAuctionUrl,
                auctionEncodedKey,
                TYPE,
                START_INDEX,
                END_INDEX,
                DELNG_DE,
                URLEncoder.encode(WHSAL_MRKT_NM,"UTF-8")
              );
		// format 구성하면서 검색 조건을 뒤에 추가하면 됨
	    
	    return new URI(url);
	}

}
