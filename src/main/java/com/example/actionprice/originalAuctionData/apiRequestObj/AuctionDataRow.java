package com.example.actionprice.originalAuctionData.apiRequestObj;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO 각 개체가 무엇인지 주석을 달아주려 했으니 너무 많아서 추후 진행
/**
 * @author 임호민
 * @created 24/10/02 11:47
 * @updated 24/10/02 11:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionDataRow {
	@JsonProperty("ROW_NUM") // 열 번호
    private int rowNum;

    @JsonProperty("DELNG_DE")  // 경락일자
    private String delngDe;

    @JsonProperty("SBID_TIME") // 경매시간
    private String sbidTime;

    @JsonProperty("AUC_SE_CODE") // 경매구분코드
    private String aucSeCode;

    @JsonProperty("AUC_SE_NM") // 경배구분코드 명
    private String aucSeNm;

    @JsonProperty("WHSAL_MRKT_NEW_CODE") // 시장코드
    private String whsalMrktNewCode;

    @JsonProperty("WHSAL_MRKT_NEW_NM") // 시장명
    private String whsalMrktNewNm;

    @JsonProperty("WHSAL_MRKT_CODE") // 구시장 코드
    private String whsalMrktCode;

    @JsonProperty("WHSAL_MRKT_NM") // 구시장명
    private String whsalMrktNm;

    @JsonProperty("CPR_INSTT_NEW_CODE") // 법인코드
    private String cprInsttNewCode;

    @JsonProperty("INSTT_NEW_NM") // 법인명
    private String insttNewNm;

    @JsonProperty("CPR_INSTT_CODE") // 구법인코드
    private String cprInsttCode;

    @JsonProperty("INSTT_NM") //구법인 명
    private String insttNm;

    @JsonProperty("LEDG_NO") // 경매원표번호
    private String ledgNo;

    @JsonProperty("SLE_SEQN") // 일련번호
    private String sleSeqn;

    @JsonProperty("CATGORY_NEW_CODE") // 부류코드
    private String catgoryNewCode;

    @JsonProperty("CATGORY_NEW_NM") // 부류명
    private String catgoryNewNm;

    @JsonProperty("CATGORY_CODE") // 구부류코드
    private String catgoryCode;

    @JsonProperty("CATGORY_NM") // 구부류명
    private String catgoryNm;

    @JsonProperty("STD_PRDLST_NEW_CODE") // 품목코드
    private String stdPrdlstNewCode;

    @JsonProperty("STD_PRDLST_NEW_NM") // 품목명
    private String stdPrdlstNewNm;

    @JsonProperty("STD_PRDLST_CODE") // 구품목코드
    private String stdPrdlstCode;

    @JsonProperty("STD_PRDLST_NM") // 구품목명
    private String stdPrdlstNm;

    @JsonProperty("STD_SPCIES_NEW_CODE") // 품종코드
    private String stdSpciesNewCode;

    @JsonProperty("STD_SPCIES_NEW_NM") // 품종명
    private String stdSpciesNewNm;

    @JsonProperty("STD_SPCIES_CODE") // 구품종코드
    private String stdSpciesCode;

    @JsonProperty("STD_SPCIES_NM") // 구품종명
    private String stdSpciesNm;

    @JsonProperty("DELNG_PRUT") // 거래단량
    private int delngPrut;

    @JsonProperty("STD_UNIT_NEW_CODE") //  단위코드
    private String stdUnitNewCode;

    @JsonProperty("STD_UNIT_NEW_NM") // 단위명
    private String stdUnitNewNm;

    @JsonProperty("STD_FRMLC_NEW_CODE") // 포장상태코드
    private String stdFrmlcNewCode;

    @JsonProperty("STD_FRMLC_NEW_NM") // 포장상태명
    private String stdFrmlcNewNm;

    @JsonProperty("STD_MG_NEW_CODE") // 크기코드
    private String stdMgNewCode;

    @JsonProperty("STD_MG_NEW_NM") // 크기명
    private String stdMgNewNm;

    @JsonProperty("STD_QLITY_NEW_CODE") // 등급코드
    private String stdQlityNewCode;

    @JsonProperty("STD_QLITY_NEW_NM") // 등급명
    private String stdQlityNewNm;

    @JsonProperty("CPR_USE_PRDLST_CODE") // 법인사용품목코드
    private String cprUsePrdlstCode;

    @JsonProperty("CPR_USE_PRDLST_NM") // 법인사용품목명
    private String cprUsePrdlstNm;

    @JsonProperty("SBID_PRIC") // 거래가격
    private int sbidPric;

    @JsonProperty("SHIPMNT_SE_CODE") // 출하구구분코드
    private String shipmntSeCode;

    @JsonProperty("SHIPMNT_SE_NM") // 출하구분명
    private String shipmntSeNm;

    @JsonProperty("STD_MTC_NEW_CODE") // 산지코드
    private String stdMtcNewCode;

    @JsonProperty("STD_MTC_NEW_NM") // 산지명
    private String stdMtcNewNm;

    @JsonProperty("CPR_MTC_CODE") // 구산지코드
    private String cprMtcCode;

    @JsonProperty("CPR_MTC_NM") // 구산지명
    private String cprMtcNm;

    @JsonProperty("DELNG_QY") //거래량
    private int delngQy;

}
