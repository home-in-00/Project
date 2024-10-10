package com.example.actionprice.newAuctionData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

/**
 * @author homin
 * @created 2024. 10. 10. 오전 10:52
 * @updated 2024. 10. 10. 오전 10:52
 * @info
 */

public class newAuctionDataRow
{
    @JsonProperty("saleDate")
    private String saleDate; // 정산일자

    @JsonProperty("whsalCd")
    private String whsalCd; // 도매시장코드

    @JsonProperty("cmpCd")
    private String cmpCd; // 법인코드

    @JsonProperty("seq")
    private String seq; // 원표 번호

    @JsonProperty("no1")
    private String no1; // 경매순서 1

    private String no2; // 경매순서 2

    private String mejang; // 매장 코드

    private String mmCd; // 매매방법 코드

    private String large; // 대분류코드

    private String mid; // 중분류코드

    private String small; // 소분류코드

    private String cmpGood; // 법인사용품목코드

    private String pumName; // 품목명

    private String goodName; // 품종명

    private int danq; // 단위 중량

    private String danCd; // 단위 코드

    private String pojCd; // 포장 코드

    private String sizeCd; // 크기 코드

    private String lvCd; // 등급 코드

    private int qty; // 물량

    private String cost; // 단가

    private String amerCd; // 중도매인코드

    private String sanCd; // 산지코드

    private String cmpSan; // 법인사용산지코드

    private String sanName; // 산지명

    private String chCd; // 구분 코드

    private String smanCd; // 수집상 코드

    private String chulNo; // 출하자 번호

    private String chulCd; // 출하자 코드

    private String chulName; // 출하자 명

    private String farmName; // 생산자 명

    private int totQty; // 총물량

    private int totAmt; // 총금액

    private String sbidtime; //낙찰시간

}
