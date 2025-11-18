package com.example.trip_maker_server.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class SchedulePlaceVO {
    
    @NotBlank
    private String placeName;
    
    @NotNull
    private Integer placeOrder;
    
    private BigDecimal cost;
    private String memo;
    private Double latitude;
    private Double longitude;
}