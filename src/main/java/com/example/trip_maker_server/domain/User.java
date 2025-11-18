package com.example.trip_maker_server.domain;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class User {
    private Integer userId;
    private String email;
    private String password;
    private String nickname;
    private String profileImageUrl;
    private String travelStyles; // (★) 1. 이 필드를 추가합니다.
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
}