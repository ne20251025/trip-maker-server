package com.example.trip_maker_server.service;

import com.example.trip_maker_server.mapper.CompanionMapper;
import com.example.trip_maker_server.vo.CompanionPostVO;
import com.example.trip_maker_server.vo.CompanionSearchDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanionService {
    
    private final CompanionMapper companionMapper;

    public List<CompanionPostVO> getAllPosts(CompanionSearchDTO searchDTO) {
        return companionMapper.selectAllPosts(searchDTO);
    }
    public CompanionPostVO getPostDetail(Integer postId) {
        return companionMapper.selectPostById(postId);
    }
 // 게시글 작성
    @Transactional
    public void createPost(Integer userId, CompanionPostVO vo) {
        vo.setUserId(userId); // 로그인한 유저 ID 주입
        companionMapper.insertPost(vo);
    }
    
    public void deletePost(Integer postId, Integer userId) {
        // 1. 기존 글 조회
        // (Mapper에 selectPostById가 이미 있으므로 재활용하거나, 가벼운 조회용 메소드 사용)
        // 여기서는 편의상 selectPostById 사용 (실무에선 count쿼리나 owner 조회 쿼리 권장)
        CompanionPostVO post = companionMapper.selectPostById(postId);
        
        if (post == null) {
            throw new RuntimeException("게시글이 존재하지 않습니다.");
        }
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        // 2. 삭제
        companionMapper.deletePost(postId);
    }
	public List<CompanionPostVO> getRecentPosts(int i) {
        return companionMapper.selectRecentPosts(4);
	}
	public List<CompanionPostVO> getMyPosts(Integer userId) {
		return companionMapper.selectPostsByUserId(userId);
	}
}