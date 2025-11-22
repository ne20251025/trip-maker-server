package com.example.trip_maker_server.vo;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ChatRoomVO {
    private Integer roomId;
    
    // (상대방 정보 - 화면에 표시할 때 필요)
    private Integer partnerId;
    private String partnerNickname;
    private String partnerProfileImage;
    
    private String lastMessage; // 마지막 대화 내용
    private LocalDateTime lastMessageTime; // 마지막 대화 시간
    private Integer unreadCount; // 안 읽은 메시지 수
}