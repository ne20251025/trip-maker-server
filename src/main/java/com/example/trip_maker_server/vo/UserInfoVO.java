package com.example.trip_maker_server.vo;

import com.example.trip_maker_server.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays; // (★) Arrays 임포트
import java.util.Collections; // (★) Collections 임포트
import java.util.List; // (★) List 임포트

@Getter
@Setter
public class UserInfoVO {
    private Integer userId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private List<String> travelStyles; // (★) 3. List<String>으로 추가
    private LocalDateTime regDate;

    // (★) 4. from 정적 메소드 수정
    public static UserInfoVO from(User user) {
        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(user.getUserId());
        vo.setEmail(user.getEmail());
        vo.setNickname(user.getNickname());
        vo.setProfileImageUrl(user.getProfileImageUrl());
        vo.setRegDate(user.getRegDate());
        
        // (★) DB의 "PLAN,FOOD" 문자열을 List<String>으로 변환
        String stylesStr = user.getTravelStyles();
        if (stylesStr == null || stylesStr.isEmpty()) {
            vo.setTravelStyles(Collections.emptyList());
        } else {
            vo.setTravelStyles(Arrays.asList(stylesStr.split(",")));
        }
        
        return vo;
    }
}