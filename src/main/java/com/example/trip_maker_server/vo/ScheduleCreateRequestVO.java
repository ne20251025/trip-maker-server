package com.example.trip_maker_server.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ScheduleCreateRequestVO {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;
    
    @NotNull(message = "시작일은 필수입니다.")
    private LocalDate startDate;
    
    @NotNull(message = "종료일은 필수입니다.")
    private LocalDate endDate;
    
    private BigDecimal totalBudget;

    // (★) 중첩된 리스트 (Day 1, Day 2...)
    private List<ScheduleDayVO> days;
}