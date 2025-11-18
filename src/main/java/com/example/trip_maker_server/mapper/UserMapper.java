package com.example.trip_maker_server.mapper;

import com.example.trip_maker_server.domain.User;
import org.apache.ibatis.annotations.Mapper;
import java.util.Optional;

@Mapper
public interface UserMapper {

    // (★) 1. 텍스트 정보(닉네임, 비번, 스타일)만 수정
    void updateUserProfileText(User user);
    
    // (★) 2. 이미지 URL만 수정
    void updateUserProfileImage(User user);

    // (★) 3. (삭제) 버그가 있던 기존 updateUser는 삭제
    // void updateUser(User user); 

    // (기존)
    Optional<User> selectUserById(Integer userId);
    Optional<User> selectUserByEmail(String email);
    int selectCountByNickname(String nickname);
    void insertUser(User user);
}