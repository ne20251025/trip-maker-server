package com.example.trip_maker_server.vo;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
public class ReservationVO {
    private Integer reservationId;
    private Integer userId;
    private Integer tourId;
    private String tourTitle; // (조회용) 투어 제목
    private String tourThumbnail; // (조회용) 썸네일
    private Integer personCount;
    private BigDecimal totalPrice;
    private LocalDate reservationDate;
    private String status;
    private LocalDateTime regDate;
}