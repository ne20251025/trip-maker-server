package com.example.trip_maker_server.controller;

import com.example.trip_maker_server.domain.User;
import com.example.trip_maker_server.service.UserService;
import com.example.trip_maker_server.vo.LoginRequestVO;
import com.example.trip_maker_server.vo.LoginResponseVO;
import com.example.trip_maker_server.vo.UserInfoVO;
import com.example.trip_maker_server.vo.ReqJoinUserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // (기존 회원가입 엔드포인트...)
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody ReqJoinUserVO vo) {
        try {
            User savedUser = userService.signup(vo);
            UserInfoVO responseVO = UserInfoVO.from(savedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseVO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    /**
     * (★신규★) 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestVO vo) {
        try {
            // 1. Service의 login 메소드 호출
            LoginResponseVO responseVO = userService.loginUser(vo);
            
            // 2. 성공 시, 200 OK와 함께 토큰 및 사용자 정보 반환
            return ResponseEntity.ok(responseVO); 
            
        } catch (BadCredentialsException e) {
            // 3. (Service에서 발생) 아이디 또는 비번 오류 시, 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // 4. 기타 서버 에러
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }
}