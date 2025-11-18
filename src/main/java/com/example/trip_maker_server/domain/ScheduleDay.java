package com.example.trip_maker_server.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleDay {
    private Integer dayId;
    private Integer scheduleId;
    private Integer dayNumber;
}