package com.example.trip_maker_server.vo;

import lombok.Getter;
import lombok.Setter;

import com.example.trip_maker_server.domain.User;

@Getter
@Setter
public class UserInfoVO {

    private Integer userId;
    private String email;
    private String nickname;
    private String regDate;
    
    public static UserInfoVO from(User user) {
        UserInfoVO userVO = new UserInfoVO();
        userVO.setUserId(user.getUserId());
        userVO.setEmail(user.getEmail());
        userVO.setNickname(user.getNickname());
        userVO.setRegDate(user.getRegDate());
        return userVO;
    }
}