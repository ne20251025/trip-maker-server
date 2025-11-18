package com.example.trip_maker_server.mapper;

import com.example.trip_maker_server.domain.ScheduleDay;
import com.example.trip_maker_server.vo.ScheduleDayDetailVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScheduleDayMapper {

	void insertScheduleDay(ScheduleDay scheduleDay);
    
    List<ScheduleDayDetailVO> selectDaysByScheduleId(Integer scheduleId);
    
    void deleteDaysByScheduleId(Integer scheduleId);
}