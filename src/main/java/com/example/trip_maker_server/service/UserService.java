package com.example.trip_maker_server.service;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.trip_maker_server.config.JwtTokenProvider;

import com.example.trip_maker_server.domain.User;
import com.example.trip_maker_server.mapper.UserMapper;
import com.example.trip_maker_server.vo.ReqJoinUserVO;
import com.example.trip_maker_server.vo.LoginResponseVO;
import com.example.trip_maker_server.vo.LoginRequestVO;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public User signup(ReqJoinUserVO vo) { // (수정) 파라미터 타입 변경
        
        // 1. 이메일 중복 검사
        if (userMapper.selectUserByEmail(vo.getEmail()).isPresent()) { // (수정)
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 2. 닉네임 중복 검사
        if (userMapper.selectCountByNickname(vo.getNickname()) > 0) { // (수정)
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 3. User 객체 생성
        User newUser = new User();
        newUser.setEmail(vo.getEmail()); // (수정)
        newUser.setPassword(passwordEncoder.encode(vo.getPassword())); // (수정)
        newUser.setNickname(vo.getNickname()); // (수정)

        // 4. DB에 저장
        userMapper.insertUser(newUser); 
        
        return newUser; // DB에서 저장/조회된 User 도메인 객체 반환
    }
    
    
    /**
     * (★신규★) 로그인
     */
    @Transactional(readOnly = true) // SELECT만 하므로 readOnly
    public LoginResponseVO loginUser(LoginRequestVO vo) {
        
        // 1. (DB) 이메일로 사용자 조회
        User user = userMapper.selectUserByEmail(vo.getEmail())
                .orElseThrow(() -> new BadCredentialsException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 2. (암호화) 요청된 비밀번호(vo.getPassword())와 DB의 암호화된 비밀번호(user.getPassword()) 비교
        // matches(평문, 암호화된문)
        if (!passwordEncoder.matches(vo.getPassword(), user.getPassword())) {
            // 비밀번호가 틀리면 예외
            throw new BadCredentialsException("이메일 또는 비밀번호가 일치하지 않습니다."); 
        }

        // 3. (JWT) 로그인 성공 시 토큰 생성
        String token = jwtTokenProvider.createToken(user.getUserId(), user.getNickname());

        // 4. (VO) 응답VO에 토큰과 닉네임, ID를 담아 반환
        return new LoginResponseVO(token, user.getNickname(), user.getUserId());
    }
}