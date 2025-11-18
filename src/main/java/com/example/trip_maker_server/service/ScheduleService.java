package com.example.trip_maker_server.service;

import com.example.trip_maker_server.domain.ScheduleInfo;
import com.example.trip_maker_server.domain.ScheduleDay;
import com.example.trip_maker_server.domain.SchedulePlace;
import com.example.trip_maker_server.mapper.ScheduleDayMapper;
import com.example.trip_maker_server.mapper.ScheduleInfoMapper;
import com.example.trip_maker_server.mapper.SchedulePlaceMapper;
import com.example.trip_maker_server.vo.ScheduleCreateRequestVO;
import com.example.trip_maker_server.vo.ScheduleDayVO;
import com.example.trip_maker_server.vo.ScheduleDetailVO;
import com.example.trip_maker_server.vo.SchedulePlaceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import com.example.trip_maker_server.vo.ScheduleDayDetailVO; // (★)
import com.example.trip_maker_server.vo.SchedulePlaceDetailVO; // (★)
import java.util.Map; // (★)
import java.util.stream.Collectors; // (★)

@Service
@RequiredArgsConstructor
public class ScheduleService {

    // (★) DB.txt 파일명 규칙에 맞게 ScheduleMapper -> ScheduleInfoMapper로 수정
    private final ScheduleInfoMapper scheduleInfoMapper; 
    private final ScheduleDayMapper scheduleDayMapper;
    private final SchedulePlaceMapper schedulePlaceMapper;

    public ScheduleDetailVO getScheduleDetail(Integer scheduleId) {
        
        // 1. (부모) 스케줄 + 작성자 정보 조회
        ScheduleDetailVO scheduleDetail = scheduleInfoMapper.selectScheduleAndUserById(scheduleId);
        
        if (scheduleDetail == null) {
            return null; // 스케줄이 없으면 종료
        }

        // 2. (자식) Day 목록 조회
        List<ScheduleDayDetailVO> days = scheduleDayMapper.selectDaysByScheduleId(scheduleId);
        
        if (days == null || days.isEmpty()) {
            scheduleDetail.setDays(List.of()); // 빈 리스트 설정 후 반환
            return scheduleDetail;
        }

        // 3. (손자) '모든' Day ID를 추출 ( [1, 2, 3] )
        List<Integer> dayIds = days.stream()
                                  .map(ScheduleDayDetailVO::getDayId)
                                  .collect(Collectors.toList());
        
        // 4. (손자) '모든' Place 목록을 DB에서 '한번에' 조회
        List<SchedulePlaceDetailVO> allPlaces = schedulePlaceMapper.selectPlacesByDayIds(dayIds);

        // (★) 5. (핵심) 손자(Place)들을 부모(Day)별로 그룹핑
        // (예: { 1: [장소A, 장소B], 2: [장소C] })
        Map<Integer, List<SchedulePlaceDetailVO>> placesByDayIdMap = allPlaces.stream()
                .collect(Collectors.groupingBy(SchedulePlaceDetailVO::getDayId));
        
        // 6. (조립) Day 목록을 돌면서, 맵에서 자기 '손자'들을 찾아 설정
        for (ScheduleDayDetailVO day : days) {
            List<SchedulePlaceDetailVO> placesForThisDay = placesByDayIdMap.getOrDefault(day.getDayId(), List.of());
            day.setPlaces(placesForThisDay);
        }

        // 7. (최종) 완성된 Day 목록을 스케줄에 설정
        scheduleDetail.setDays(days);
        
        return scheduleDetail;
    }
    
    @Transactional
    public ScheduleInfo updateSchedule(Integer scheduleId, Integer userId, ScheduleCreateRequestVO vo) {
        
        // (★) (보안) 이 스케줄이 '내 것'이 맞는지 확인 (간단한 방법)
        ScheduleDetailVO existingSchedule = scheduleInfoMapper.selectScheduleAndUserById(scheduleId);
        if (existingSchedule == null || !existingSchedule.getUserId().equals(userId)) {
            // (내 스케줄이 아니거나, 존재하지 않으면 예외 발생)
            throw new RuntimeException("수정 권한이 없거나 존재하지 않는 스케줄입니다.");
        }

        // 1. (부모) T_SCHEDULE_INFO 덮어쓰기
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setScheduleId(scheduleId); // (★) ID 지정
        scheduleInfo.setTitle(vo.getTitle());
        scheduleInfo.setStartDate(vo.getStartDate());
        scheduleInfo.setEndDate(vo.getEndDate());
        scheduleInfo.setTotalBudget(vo.getTotalBudget());
        
        scheduleInfoMapper.updateScheduleInfo(scheduleInfo);

        // 2. (기존 데이터 삭제) '손자' -> '자식' 순서로 삭제
        schedulePlaceMapper.deletePlacesByScheduleId(scheduleId);
        scheduleDayMapper.deleteDaysByScheduleId(scheduleId);

        // 3. (데이터 재삽입) 공통 함수 호출
        insertDaysAndPlaces(scheduleId, vo.getDays());
        
        // (수정된 정보 반환)
        scheduleInfo.setUserId(userId); // (userId는 update 쿼리에 없었으므로 세팅)
        return scheduleInfo;
    }
    
    public List<ScheduleInfo> getSchedulesByUserId(Integer userId) {
        return scheduleInfoMapper.selectSchedulesByUserId(userId);
    }
    
    private void insertDaysAndPlaces(Integer scheduleId, List<ScheduleDayVO> daysVO) {
        if (daysVO == null) return;

        for (ScheduleDayVO dayVO : daysVO) {
            ScheduleDay scheduleDay = new ScheduleDay();
            scheduleDay.setScheduleId(scheduleId);
            scheduleDay.setDayNumber(dayVO.getDayNumber());
            
            scheduleDayMapper.insertScheduleDay(scheduleDay);
            Integer dayId = scheduleDay.getDayId(); // (★) 새로 생성된 dayId

            if (dayVO.getPlaces() != null) {
                for (SchedulePlaceVO placeVO : dayVO.getPlaces()) {
                    SchedulePlace place = new SchedulePlace();
                    place.setDayId(dayId);
                    place.setPlaceName(placeVO.getPlaceName());
                    place.setPlaceOrder(placeVO.getPlaceOrder());
                    place.setCost(placeVO.getCost());
                    place.setMemo(placeVO.getMemo());
                    place.setLatitude(placeVO.getLatitude());
                    place.setLongitude(placeVO.getLongitude());
                    
                    schedulePlaceMapper.insertSchedulePlace(place);
                }
            }
        }
    }
    
    @Transactional
    public ScheduleInfo createSchedule(Integer userId, ScheduleCreateRequestVO vo) {
        
        // 1. (부모) T_SCHEDULE_INFO 테이블에 저장
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setUserId(userId);
        scheduleInfo.setTitle(vo.getTitle());
        scheduleInfo.setStartDate(vo.getStartDate());
        scheduleInfo.setEndDate(vo.getEndDate());
        scheduleInfo.setTotalBudget(vo.getTotalBudget());
        
        scheduleInfoMapper.insertScheduleInfo(scheduleInfo);
        Integer scheduleId = scheduleInfo.getScheduleId(); 

        // 2. (자식/손자) 공통 함수 호출
        insertDaysAndPlaces(scheduleId, vo.getDays());
        
        return scheduleInfo;
    }
    
    @Transactional
    public void deleteSchedule(Integer scheduleId, Integer userId) {
        
        // (★) 1. (보안) 이 스케줄이 '내 것'이 맞는지 확인
        ScheduleDetailVO existingSchedule = scheduleInfoMapper.selectScheduleAndUserById(scheduleId);
        if (existingSchedule == null) {
            throw new RuntimeException("존재하지 않는 스케줄입니다.");
        }
        if (!existingSchedule.getUserId().equals(userId)) {
            // (내 스케줄이 아니면 예외 발생)
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        // (★) 2. (삭제) '손자' -> '자식' -> '부모' 순서로 삭제
        // (FK 제약 조건 때문)
        schedulePlaceMapper.deletePlacesByScheduleId(scheduleId);
        scheduleDayMapper.deleteDaysByScheduleId(scheduleId);
        scheduleInfoMapper.deleteScheduleById(scheduleId);
    }
}