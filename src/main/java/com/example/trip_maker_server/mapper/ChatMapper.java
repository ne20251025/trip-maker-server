package com.example.trip_maker_server.mapper;

import com.example.trip_maker_server.vo.ChatMessageVO;
import com.example.trip_maker_server.vo.ChatRoomVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChatMapper {
    
    // 1. 나와 상대방 사이의 방이 이미 있는지 확인
    Integer findRoomIdByUsers(@Param("userA") Integer userA, @Param("userB") Integer userB);
    
    // 2. 채팅방 생성
    void insertChatRoom(@Param("userA") Integer userA, @Param("userB") Integer userB);
    
    // 3. 내 채팅방 목록 조회 (상대방 정보 포함)
    List<ChatRoomVO> selectMyChatRooms(Integer myUserId);
    
    // 4. 메시지 저장
    void insertMessage(ChatMessageVO message);
    
    // 5. 특정 방의 메시지 기록 조회
    List<ChatMessageVO> selectMessagesByRoomId(Integer roomId);
    
    int countTotalUnreadMessages(Integer userId);
    
    void updateReadStatus(@Param("roomId") Integer roomId, @Param("myUserId") Integer myUserId);
}