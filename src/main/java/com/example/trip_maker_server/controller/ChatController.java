package com.example.trip_maker_server.controller;

import com.example.trip_maker_server.service.ChatService;
import com.example.trip_maker_server.vo.ChatMessageVO;
import com.example.trip_maker_server.vo.ChatRoomVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 1. 채팅방 생성/조회 (동행 상세 -> 채팅 신청 클릭 시)
    @PostMapping("/room")
    public ResponseEntity<?> createRoom(@RequestBody Map<String, Integer> body, Authentication auth) {
        Integer myId = getUserId(auth);
        Integer partnerId = body.get("partnerId");
        
        Integer roomId = chatService.createOrGetChatRoom(myId, partnerId);
        return ResponseEntity.ok(roomId);
    }

    // 2. 내 채팅방 목록 조회
    @GetMapping("/rooms")
    public ResponseEntity<?> getMyRooms(Authentication auth) {
        Integer myId = getUserId(auth);
        List<ChatRoomVO> rooms = chatService.getMyChatRooms(myId);
        return ResponseEntity.ok(rooms);
    }

    // (유틸) 유저 ID 추출
    private Integer getUserId(Authentication auth) {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return Integer.parseInt(userDetails.getUsername());
    }
    
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable("roomId") Integer roomId, Authentication auth) {
        Integer myId = getUserId(auth);
        
        // (★) 메시지 가져오기 전에 '읽음'으로 변경
        chatService.markAsRead(roomId, myId);
        
        List<ChatMessageVO> messages = chatService.getMessages(roomId);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/unread-count")
    public ResponseEntity<?> getTotalUnreadCount(Authentication auth) {
        // 로그인이 안 되어 있으면 0 리턴
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.ok(0);
        }
        Integer myId = getUserId(auth);
        int count = chatService.getTotalUnreadCount(myId);
        return ResponseEntity.ok(count);
    }
}