package com.example.trip_maker_server.mapper;
import com.example.trip_maker_server.vo.ReservationVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ReservationMapper {
    void insertReservation(ReservationVO vo);
    List<ReservationVO> selectMyReservations(Integer userId);
}