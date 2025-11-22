package com.example.trip_maker_server.service;

import com.example.trip_maker_server.mapper.TourMapper;
import com.example.trip_maker_server.vo.TourProductVO;
import com.example.trip_maker_server.vo.TourSearchDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TourService {

    private final TourMapper tourMapper;

    public List<TourProductVO> getTours(TourSearchDTO searchDTO) {
        return tourMapper.selectTours(searchDTO);
    }
    
    public TourProductVO getTourDetail(Integer tourId) {
        return tourMapper.selectTourById(tourId);
    }

	public List<TourProductVO> getRecentTours(int i) {
		return tourMapper.selectRecentTours(i);
	}
}