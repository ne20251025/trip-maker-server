package com.example.trip_maker_server.controller;

import com.example.trip_maker_server.service.ChatService;
import com.example.trip_maker_server.vo.ChatMessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    // (★) 클라이언트가 "/pub/chat/send"로 메시지를 보내면 여기서 처리
    @MessageMapping("/chat/send")
    public void sendMessage(ChatMessageVO message) {
        // 1. 메시지 시간 설정
        message.setSendTime(LocalDateTime.now());

        // 2. DB 저장
        chatService.saveMessage(message);

        // 3. 해당 방(/sub/chat/room/{roomId})을 구독 중인 사람들에게 메시지 발송
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }
}