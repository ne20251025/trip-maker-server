package com.example.trip_maker_server.mapper;

import com.example.trip_maker_server.vo.TourProductVO;
import com.example.trip_maker_server.vo.TourSearchDTO;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TourMapper {
    
	List<TourProductVO> selectTours(TourSearchDTO searchDTO);
    
    TourProductVO selectTourById(Integer tourId);
}
