package com.example.actionprice.originalAuctionData.apiRequestObj;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 연상훈
 * @created 24/10/01 20:10
 * @updated 24/10/02 11:35
 * @info api에서 제공하는 객체 형태 그대로 가져옴
 * @see : https://data.mafra.go.kr/opendata/data/indexOpenDataDetail.do?data_id=20151117000000000533
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionDataContent {
	
	@JsonProperty("totalCnt")
	private int totalCnt;
	
	@JsonProperty("startRow")
    private int startRow;
	
    @JsonProperty("endRow")
    private int endRow;
    
    @JsonProperty("result")
    private AuctionDataResultOfReq result;
    
    @JsonProperty("row")
    private List<AuctionDataRow> row;
}
