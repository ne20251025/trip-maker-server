package com.example.trip_maker_server.controller;

import com.example.trip_maker_server.service.TourService;
import com.example.trip_maker_server.vo.TourProductVO;
import com.example.trip_maker_server.vo.TourSearchDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    @GetMapping
    public ResponseEntity<?> getTours(@ModelAttribute TourSearchDTO searchDTO) {
        try {
            List<TourProductVO> tours = tourService.getTours(searchDTO);
            return ResponseEntity.ok(tours);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("투어 목록 조회 중 오류 발생");
        }
    }
    
    @GetMapping("/{tourId}")
    public ResponseEntity<?> getTourById(@PathVariable("tourId") Integer tourId) {
        try {
            TourProductVO tour = tourService.getTourDetail(tourId);
            if (tour == null) {
                return ResponseEntity.status(404).body("투어 상품을 찾을 수 없습니다.");
            }
            return ResponseEntity.ok(tour);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("투어 상세 조회 중 오류 발생");
        }
    }
}