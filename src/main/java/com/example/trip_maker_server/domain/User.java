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
    private String regDate;
    private String updateDate;
}