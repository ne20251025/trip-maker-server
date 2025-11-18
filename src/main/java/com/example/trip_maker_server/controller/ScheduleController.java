package com.example.trip_maker_server.controller;

import com.example.trip_maker_server.domain.ScheduleInfo;
import com.example.trip_maker_server.service.ScheduleService;
import com.example.trip_maker_server.vo.ScheduleCreateRequestVO;
import com.example.trip_maker_server.vo.ScheduleDetailVO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * (★) '스케줄 저장' API
     * (POST /api/schedules)
     */
    @PostMapping
    public ResponseEntity<?> createSchedule(
            Authentication authentication,
            @Valid @RequestBody ScheduleCreateRequestVO vo) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }
        
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Integer userId = Integer.parseInt(userDetails.getUsername());

            ScheduleInfo newSchedule = scheduleService.createSchedule(userId, vo);

            // (★) 성공 시, 생성된 ScheduleInfo 객체(ID 포함) 반환
            return ResponseEntity.status(HttpStatus.CREATED).body(newSchedule);

        } catch (Exception e) {
            e.printStackTrace(); // (★) 에러 로그 확인용
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("스케줄 저장 중 오류 발생: " + e.getMessage());
        }
    }
    
    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> getScheduleById(@PathVariable("scheduleId") Integer scheduleId) {
        try {
            ScheduleDetailVO scheduleDetail = scheduleService.getScheduleDetail(scheduleId);
            
            if (scheduleDetail == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("스케줄을 찾을 수 없습니다.");
            }
            
            // (★) (참고) scheduleDetail.getDays()가 비어있을 수 있음
            // (-> 장소가 없는 스케줄일 경우)

            return ResponseEntity.ok(scheduleDetail);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("상세 조회 중 오류 발생: " + e.getMessage());
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getMySchedules(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }
        
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Integer userId = Integer.parseInt(userDetails.getUsername());

            List<ScheduleInfo> schedules = scheduleService.getSchedulesByUserId(userId);
            
            return ResponseEntity.ok(schedules);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("스케줄 조회 중 오류 발생: " + e.getMessage());
        }
    }
    
    @PutMapping("/{scheduleId}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable("scheduleId") Integer scheduleId,
            Authentication authentication,
            @Valid @RequestBody ScheduleCreateRequestVO vo) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }
        
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Integer userId = Integer.parseInt(userDetails.getUsername());

            // (★) 서비스의 'updateSchedule' 메소드 호출
            ScheduleInfo updatedSchedule = scheduleService.updateSchedule(scheduleId, userId, vo);

            return ResponseEntity.ok(updatedSchedule);

        } catch (RuntimeException e) {
            // (★) (서비스에서 던진 '권한 없음' 예외 처리)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("스케줄 수정 중 오류 발생: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable("scheduleId") Integer scheduleId,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }
        
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Integer userId = Integer.parseInt(userDetails.getUsername());

            // (★) 서비스의 'deleteSchedule' 메소드 호출
            scheduleService.deleteSchedule(scheduleId, userId);

            // (삭제 성공 시 204 No Content 반환)
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            // (★) (서비스에서 던진 '권한 없음' 예외 처리)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("스케줄 삭제 중 오류 발생: " + e.getMessage());
        }
    }
}