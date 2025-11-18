package com.example.trip_maker_server.controller;

import com.example.trip_maker_server.domain.User;
import com.example.trip_maker_server.service.UserService;
import com.example.trip_maker_server.vo.*;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // (★) Authentication 임포트
import org.springframework.security.core.userdetails.UserDetails; // (★) UserDetails 임포트

import org.springframework.web.bind.annotation.RequestParam; // (★) RequestParam 임포트
import org.springframework.web.multipart.MultipartFile; // (★) MultipartFile 임포트
import java.io.IOException;

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
    
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }

        try {
            // (★) '인증 정보'에서 사용자 ID(pk)를 추출합니다.
            // (JwtTokenProvider에서 subject에 'userId'를 저장했었음)
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Integer userId = Integer.parseInt(userDetails.getUsername()); // (getUsername()이 userId를 반환)

            // 2. Service 호출
            UserInfoVO userProfile = userService.getUserProfile(userId);

            // 3. 200 OK와 함께 프로필 정보 반환
            return ResponseEntity.ok(userProfile);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        }
    }
    
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody ProfileUpdateRequestVO vo) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }

        try {
            // 1. 인증 정보에서 사용자 ID 추출
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Integer userId = Integer.parseInt(userDetails.getUsername());

            // 2. Service 호출
            UserInfoVO updatedProfile = userService.updateUserProfile(userId, vo);

            // 3. 200 OK와 함께 '변경된' 프로필 정보 반환
            return ResponseEntity.ok(updatedProfile);

        } catch (IllegalArgumentException e) {
            // (예: 닉네임 중복)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (BadCredentialsException e) {
            // (예: 현재 비밀번호 불일치)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        }
    }
    
    @PostMapping("/me/image")
    public ResponseEntity<?> updateMyProfileImage(
            Authentication authentication,
            // (★) 'profileImage'라는 이름으로 넘어온 파일을 받음
            @RequestParam("profileImage") MultipartFile file) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }
        
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미지 파일이 비어있습니다.");
        }

        try {
            // 1. 인증 정보에서 사용자 ID 추출
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Integer userId = Integer.parseInt(userDetails.getUsername());

            // 2. Service 호출 (파일 저장 및 DB 업데이트)
            String newImageUrl = userService.updateProfileImage(userId, file);

            // 3. 200 OK와 함께 '새 이미지 URL' 반환
            // (React가 이 URL로 미리보기를 갱신할 수 있음)
            return ResponseEntity.ok(newImageUrl);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 중 오류 발생: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        }
    }
}