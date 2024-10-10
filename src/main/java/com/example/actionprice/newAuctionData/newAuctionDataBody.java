package com.example.actionprice.newAuctionData;

import com.example.actionprice.originalAuctionData.apiRequestObj.AuctionDataContent;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class newAuctionDataBody
{
    @JsonProperty("")
    private AuctionDataContent content;
}