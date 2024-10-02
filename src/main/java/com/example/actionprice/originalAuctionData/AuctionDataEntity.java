package com.example.actionprice.originalAuctionData;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

//TODO entity에 대한 논의 필요
/**
* @author 연상훈
* @created 24/10/01 21:10
* @updated 24/10/01 21:10
* @info entity에 대한 논의 필요. 지금 테이블 생성 권한이 없어서 만들려고 시도했다간 오류 생길 테니 주석 처리 해둠. 지금은 레포지토리도 만들면 안 됨
* @see : https://data.mafra.go.kr/opendata/data/indexOpenDataDetail.do?data_id=20151117000000000533
*/
// @Table(name = "auction_data")
// @Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class AuctionDataEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
