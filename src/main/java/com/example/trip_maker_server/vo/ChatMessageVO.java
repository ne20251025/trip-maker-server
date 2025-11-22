package com.example.trip_maker_server.vo;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageVO {
    private Long messageId;
    private Integer roomId;
    private Integer senderId;
    private String content;
    private LocalDateTime sendTime;
    
    // (화면 표시용: 내가 보낸건지 여부)
    private Boolean isMine; 
}