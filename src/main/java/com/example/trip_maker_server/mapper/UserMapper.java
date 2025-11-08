package com.example.trip_maker_server.mapper;

import com.example.trip_maker_server.domain.User;
import org.apache.ibatis.annotations.Mapper;
import java.util.Optional;

@Mapper
public interface UserMapper {

    // (수정된 이름) 이메일로 사용자 찾기 (비밀번호 검증용)
    Optional<User> selectUserByEmail(String email);

    // (수정된 이름) 닉네임 중복 검사
    int selectCountByNickname(String nickname);

    // (수정된 이름) 사용자 정보 저장
    void insertUser(User user);
    
}