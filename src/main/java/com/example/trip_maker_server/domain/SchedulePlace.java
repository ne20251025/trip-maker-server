package com.example.trip_maker_server.domain;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal; // (★) COST가 DECIMAL이므로

@Getter
@Setter
public class SchedulePlace {
    private Integer placeId;
    private Integer dayId;
    private String placeName;
    private Integer placeOrder;
    private BigDecimal cost; // (★) DECIMAL
    private String memo;
    private Double latitude;
    private Double longitude;
}