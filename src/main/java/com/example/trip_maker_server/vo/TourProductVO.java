package com.example.trip_maker_server.vo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class TourProductVO {
    private Integer tourId;
    private String title;
    private String locationTag; // (예: "부산", "강원")
    private BigDecimal price;
    private String thumbnailUrl;
    private String guideInfo;
    private String description;
}