package com.example.trip_maker_server.domain;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal; // (★) TOTAL_BUDGET이 DECIMAL이므로
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleInfo {
    private Integer scheduleId;
    private Integer userId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalBudget; // (★) DECIMAL
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
}