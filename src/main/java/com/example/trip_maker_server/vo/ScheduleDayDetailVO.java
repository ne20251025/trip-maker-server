package com.example.trip_maker_server.vo;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ScheduleDayDetailVO {
    // (T_SCHEDULE_DAY 의 컬럼)
    private Integer dayId;
    private Integer dayNumber;
    
    // (★) 중첩된 장소 목록
    private List<SchedulePlaceDetailVO> places;
}