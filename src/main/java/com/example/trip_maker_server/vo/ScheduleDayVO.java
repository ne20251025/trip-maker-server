package com.example.trip_maker_server.vo;

import jakarta.validation.constraints.NotNull; // (★) NotNull 추가
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ScheduleDayVO {
    
    @NotNull
    private Integer dayNumber;

    // (★) 중첩된 리스트 (장소 1, 장소 2...)
    private List<SchedulePlaceVO> places;
}