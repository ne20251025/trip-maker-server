package com.example.trip_maker_server.service;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.trip_maker_server.config.JwtTokenProvider;

import com.example.trip_maker_server.domain.User;
import com.example.trip_maker_server.mapper.UserMapper;
import com.example.trip_maker_server.vo.*;
import org.springframework.util.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Value("${upload.path}")
    private String uploadPath;

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
    
   @Transactional(readOnly = true)
   public UserInfoVO getUserProfile(Integer userId) {
       
       // 1. ID로 DB에서 User (Domain 객체) 조회
       User user = userMapper.selectUserById(userId)
               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));

       // 2. (★중요★) 
       //    DB에서 가져온 User(비번 포함)를 
       //    React에 보낼 UserInfoVO(비번 없음)로 변환
       return UserInfoVO.from(user);
   }
   @Transactional
   public UserInfoVO updateUserProfile(Integer userId, ProfileUpdateRequestVO vo) {
       
       User user = userMapper.selectUserById(userId)
               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

       // (닉네임 변경 처리)
       if (StringUtils.hasText(vo.getNickname()) && !vo.getNickname().equals(user.getNickname())) {
           if (userMapper.selectCountByNickname(vo.getNickname()) > 0) {
               throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
           }
           user.setNickname(vo.getNickname());
       }

       // (비밀번호 변경 처리)
       if (StringUtils.hasText(vo.getNewPassword())) {
           if (vo.getCurrentPassword() == null || !passwordEncoder.matches(vo.getCurrentPassword(), user.getPassword())) {
               throw new BadCredentialsException("현재 비밀번호가 일치하지 않습니다.");
           }
           user.setPassword(passwordEncoder.encode(vo.getNewPassword()));
       }
       
       // (★) 여행 스타일 변경 처리 (List -> "PLAN,FOOD" String)
       if (vo.getTravelStyles() != null) {
           String styles = String.join(",", vo.getTravelStyles());
           user.setTravelStyles(styles);
       }

       // (★) 텍스트 전용 쿼리 호출
       userMapper.updateUserProfileText(user);
       
       return UserInfoVO.from(user);
   }
   
   /**
    * (★) 2. 이미지 프로필 수정 (이미지 버그 수정)
    */
   @Transactional
   public String updateProfileImage(Integer userId, MultipartFile file) throws IOException {
       
       User user = userMapper.selectUserById(userId)
               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

       // (기존 파일 삭제 로직 ...)
       if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
           String oldFileName = user.getProfileImageUrl().replace("/uploads/", "");
           Path oldFilePath = Paths.get(uploadPath + oldFileName);
           if (Files.exists(oldFilePath)) {
               try {
                   Files.delete(oldFilePath);
               } catch (IOException e) {
                   // (로그만 남기고 계속 진행)
                   System.err.println("기존 파일 삭제 실패: " + oldFilePath);
               }
           }
       }
       
       // (새 파일 저장 로직 ...)
       String originalFileName = file.getOriginalFilename();
       String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
       String savedFileName = UUID.randomUUID().toString() + extension;
       Path destinationPath = Paths.get(uploadPath + savedFileName);
       file.transferTo(destinationPath);
       
       String webAccessUrl = "/uploads/" + savedFileName;
       
       // (★) User 객체에 URL만 설정
       user.setProfileImageUrl(webAccessUrl);

       // (★) 이미지 전용 쿼리 호출
       userMapper.updateUserProfileImage(user);

       return webAccessUrl;
   }
}