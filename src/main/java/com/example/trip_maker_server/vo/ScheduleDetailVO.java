package com.example.trip_maker_server.vo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ScheduleDetailVO {
    // (T_SCHEDULE_INFO 의 컬럼)
    private Integer scheduleId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalBudget;
    
    // (★) (T_USER_INFO ) 작성자 정보 (JOIN)
    private Integer userId;
    private String nickname;
    private String profileImageUrl;

    // (★) 중첩된 Day 목록
    private List<ScheduleDayDetailVO> days;
}