package com.example.trip_maker_server.vo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class SchedulePlaceDetailVO {
    private Integer placeId;
    private Integer dayId;
    private String placeName;
    private Integer placeOrder;
    private BigDecimal cost;
    private String memo;
    private Double latitude;
    private Double longitude;
}