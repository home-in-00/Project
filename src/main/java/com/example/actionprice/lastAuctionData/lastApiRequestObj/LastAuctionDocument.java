package com.example.actionprice.lastAuctionData.lastApiRequestObj;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastAuctionDocument {

    @JsonProperty("LastAuctionCondition")
    private LastAuctionCondition condition;

    @JsonProperty("LastAuctionData")
    private LastAuctionData data;
}
