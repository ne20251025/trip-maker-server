package com.example.trip_maker_server.mapper;

import com.example.trip_maker_server.domain.ScheduleInfo;
import com.example.trip_maker_server.vo.ScheduleDetailVO;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ScheduleInfoMapper {
    void insertScheduleInfo(ScheduleInfo scheduleInfo);
    
    List<ScheduleInfo> selectSchedulesByUserId(Integer userId);
    
    ScheduleDetailVO selectScheduleAndUserById(Integer scheduleId);
    
    void updateScheduleInfo(ScheduleInfo scheduleInfo);
    
    void deleteScheduleById(Integer scheduleId);
}