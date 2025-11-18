package com.example.trip_maker_server.vo;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class CompanionPostVO {
    private Integer postId;
    private Integer userId;
    private String nickname; // (★) 작성자 닉네임 (DB 조회 시 JOIN)
    private Integer scheduleId;
    private String title;
    private String content;
    private String tags;     // (★) "20대,맛집" 처럼 콤마로 구분된 문자열
    private String status;   // "OPEN" or "CLOSED"
    private LocalDateTime regDate;
    private String scheduleTitle; 
    private String schedulePeriod;
    private String profileImageUrl;
}