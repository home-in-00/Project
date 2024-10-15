package com.example.actionprice.oldAuctionData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

//TODO entity에 대한 논의 필요
/**
* @author 연상훈
* @created 24/10/01 21:10
* @updated 24/10/01 21:10
* @info entity에 대한 논의 필요. 지금 테이블 생성 권한이 없어서 만들려고 시도했다간 오류 생길 테니 주석 처리 해둠. 지금은 레포지토리도 만들면 안 됨
* @see : https://data.mafra.go.kr/opendata/data/indexOpenDataDetail.do?data_id=20151117000000000533
*/

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class OldAuctionDataEntity {

    private Long dataId;

    private LocalDate DELNG_DE;  // 경락일자

    private String WHSAL_MRKT_NEW_CODE; // 시장코드

    private String WHSAL_MRKT_NEW_NM; // 시장명

    private String WHSAL_MRKT_CODE; // 구시장 코드

    private String WHSAL_MRKT_NM; // 구시장명

    private String CATGORY_NEW_CODE; // 부류코드

    private String CATGORY_NEW_NM; // 부류명

    private String CATGORY_CODE; // 구부류코드

    private String CATGORY_NM; // 구부류명

    private String STD_PRDLST_NEW_CODE; // 품목코드

    private String STD_PRDLST_NEW_NM; // 품목명

    private String STD_PRDLST_CODE; // 구품목코드

    private String STD_PRDLST_NM; // 구품목명

    private String STD_MG_NEW_CODE;; //크기명

    private int DELNG_PRUT; // 거래단량

    private String STD_UNIT_NEW_NM; // 단위명

    private String STD_QLITY_NEW_CODE; // 등급코드

    private int SBID_PRIC; // 거래가격

    private int DELNG_QY; //거래량

}