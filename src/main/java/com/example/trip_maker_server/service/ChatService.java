package com.example.trip_maker_server.service;

import com.example.trip_maker_server.mapper.ChatMapper;
import com.example.trip_maker_server.vo.ChatMessageVO;
import com.example.trip_maker_server.vo.ChatRoomVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMapper chatMapper;

    // (1) 채팅방 생성 (또는 기존 방 조회) - "채팅 신청하기" 눌렀을 때
    @Transactional
    public Integer createOrGetChatRoom(Integer myUserId, Integer partnerId) {
        // 이미 방이 있는지 확인
        Integer existingRoomId = chatMapper.findRoomIdByUsers(myUserId, partnerId);
        if (existingRoomId != null) {
            return existingRoomId;
        }
        // 없으면 새로 생성
        // (Mapper XML에서 useGeneratedKeys 설정을 안했다면, insert 후 객체에 ID가 안 담길 수 있으므로 주의.
        //  여기서는 간단하게 insert 호출하고 다시 조회하는 방식을 써도 되지만, 
        //  XML에 useGeneratedKeys="true"를 설정했다고 가정합니다.)
        //  하지만 insertChatRoom은 void라서 ID를 리턴받으려면 파라미터 객체를 써야 하는데,
        //  여기선 간단히 insert 후 다시 select 하겠습니다.
        chatMapper.insertChatRoom(myUserId, partnerId);
        return chatMapper.findRoomIdByUsers(myUserId, partnerId);
    }

    public int getTotalUnreadCount(Integer userId) {
        return chatMapper.countTotalUnreadMessages(userId);
    }
    
    // (2) 내 채팅방 목록
    public List<ChatRoomVO> getMyChatRooms(Integer userId) {
        return chatMapper.selectMyChatRooms(userId);
    }

    // (3) 메시지 저장
    public ChatMessageVO saveMessage(ChatMessageVO message) {
        chatMapper.insertMessage(message);
        return message; // ID와 시간(DB 기본값)이 채워진 상태면 좋음
    }

    @Transactional
    public void markAsRead(Integer roomId, Integer myUserId) {
        chatMapper.updateReadStatus(roomId, myUserId);
    }
    
    // (4) 대화 기록 조회
    public List<ChatMessageVO> getMessages(Integer roomId) {
        return chatMapper.selectMessagesByRoomId(roomId);
    }
}