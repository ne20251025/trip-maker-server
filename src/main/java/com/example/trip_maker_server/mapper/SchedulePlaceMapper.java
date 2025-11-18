package com.example.trip_maker_server.mapper;

import com.example.trip_maker_server.domain.SchedulePlace;
import com.example.trip_maker_server.vo.SchedulePlaceDetailVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SchedulePlaceMapper {

	void insertSchedulePlace(SchedulePlace schedulePlace);
    
    List<SchedulePlaceDetailVO> selectPlacesByDayIds(List<Integer> dayIds);
    
    void deletePlacesByScheduleId(Integer scheduleId);
}