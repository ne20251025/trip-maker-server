package com.example.trip_maker_server.vo; // 또는 dto

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TourSearchDTO {
    private String location;   // 지역 (예: "부산")
    private String category;   // 테마 (예: "activity")
    private String priceRange; // 가격대 ("low", "mid", "high")
    private String date;       // 날짜 (※참고: 현재 DB 구조상 날짜 필터링은 복잡하므로 UI만 받고 패스)
}