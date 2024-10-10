package com.example.actionprice.newAuctionData;


import com.example.actionprice.originalAuctionData.apiRequestObj.AuctionDataResultOfReq;
import com.example.actionprice.originalAuctionData.apiRequestObj.AuctionDataRow;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class newAuctionDataContent
{

    @JsonProperty("totalCnt")
    private int totalCnt;

    @JsonProperty("startRow")
    private int startRow;

    @JsonProperty("endRow")
    private int endRow;

    @JsonProperty("result")
    private newAuctionDataResultOfReq result;

    @JsonProperty("row")
    private List<newAuctionDataRow> row;


}
