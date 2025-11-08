package com.example.trip_maker_server.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor 
public class LoginResponseVO {

    private String accessToken;
    private String nickname;
    private Integer userId;
}