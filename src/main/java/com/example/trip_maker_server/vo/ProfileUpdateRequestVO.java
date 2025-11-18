package com.example.trip_maker_server.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.List; // (★) List 임포트

@Getter
@Setter
public class ProfileUpdateRequestVO {

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 15, message = "닉네임은 2자에서 15자 사이여야 합니다.")
    private String nickname;

    private String currentPassword;

    @Size(min = 8, message = "새 비밀번호는 8자 이상이어야 합니다.")
    private String newPassword;

    // (★) 2. 여행 스타일 리스트 (예: ["PLAN", "FOOD"])
    private List<String> travelStyles; 
}