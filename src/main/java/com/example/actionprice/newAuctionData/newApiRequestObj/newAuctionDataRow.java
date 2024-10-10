package com.example.actionprice.newAuctionData.newApiRequestObj;

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
 * @info 로우데이터
 */
public class newAuctionDataRow
{

    @JsonProperty("rn")
    private int rn; // 번호

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

    @JsonProperty("no2")
    private String no2; // 경매순서 2

    @JsonProperty("mejang")
    private String mejang; // 매장 코드

    @JsonProperty("mmCd")
    private String mmCd; // 매매방법 코드

    @JsonProperty("large")
    private String large; // 대분류코드

    @JsonProperty("mid")
    private String mid; // 중분류코드

    @JsonProperty("small")
    private String small; // 소분류코드

    @JsonProperty("cmpGood")
    private String cmpGood; // 법인사용품목코드

    @JsonProperty("pumName")
    private String pumName; // 품목명

    @JsonProperty("goodName")
    private String goodName; // 품종명

    @JsonProperty("danq")
    private int danq; // 단위 중량

    @JsonProperty("danCd")
    private String danCd; // 단위 코드

    @JsonProperty("pojCd")
    private String pojCd; // 포장 코드

    @JsonProperty("sizeCd")
    private String sizeCd; // 크기 코드

    @JsonProperty("lvCd")
    private String lvCd; // 등급 코드

    @JsonProperty("qty")
    private int qty; // 물량

    @JsonProperty("cost")
    private String cost; // 단가

    @JsonProperty("amerCd")
    private String amerCd; // 중도매인코드

    @JsonProperty("sanCd")
    private String sanCd; // 산지코드

    @JsonProperty("cmpSan")
    private String cmpSan; // 법인사용산지코드

    @JsonProperty("sanName")
    private String sanName; // 산지명

    @JsonProperty("chCd")
    private String chCd; // 구분 코드

    @JsonProperty("smanCd")
    private String smanCd; // 수집상 코드

    @JsonProperty("chulNo")
    private String chulNo; // 출하자 번호

    @JsonProperty("chulCd")
    private String chulCd; // 출하자 코드

    @JsonProperty("chulName")
    private String chulName; // 출하자 명

    @JsonProperty("farmName")
    private String farmName; // 생산자 명

    @JsonProperty("totQty")
    private int totQty; // 총물량

    @JsonProperty("totAmt")
    private int totAmt; // 총금액

    @JsonProperty("sbidtime")
    private String sbidtime; //낙찰시간

}
