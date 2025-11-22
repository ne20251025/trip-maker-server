package com.example.trip_maker_server.controller;

import com.example.trip_maker_server.mapper.ReservationMapper;
import com.example.trip_maker_server.vo.ReservationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationMapper reservationMapper;

    // 내 예약 조회
    @GetMapping("/me")
    public ResponseEntity<?> getMyReservations(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        Integer userId = Integer.parseInt(((UserDetails) auth.getPrincipal()).getUsername());
        return ResponseEntity.ok(reservationMapper.selectMyReservations(userId));
    }

    // 예약 하기
    @PostMapping
    public ResponseEntity<?> makeReservation(@RequestBody ReservationVO vo, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        Integer userId = Integer.parseInt(((UserDetails) auth.getPrincipal()).getUsername());
        vo.setUserId(userId);
        reservationMapper.insertReservation(vo);
        return ResponseEntity.ok("예약 성공");
    }
}