package com.example.trip_maker_server.controller;

import com.example.trip_maker_server.service.CompanionService;
import com.example.trip_maker_server.vo.CompanionPostVO;
import com.example.trip_maker_server.vo.CompanionSearchDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companions")
@RequiredArgsConstructor
public class CompanionController {

    private final CompanionService companionService;

    // 1. 동행 글 쓰기 (로그인 필수)
    @PostMapping
    public ResponseEntity<?> createPost(Authentication authentication, @RequestBody CompanionPostVO vo) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Integer userId = Integer.parseInt(userDetails.getUsername());

            companionService.createPost(userId, vo);
            return ResponseEntity.ok("게시글이 등록되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록 중 오류 발생");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPosts(@ModelAttribute CompanionSearchDTO searchDTO) {
        try {
            // (★수정★) 태그 문자열을 리스트로 변환
            if (searchDTO.getTags() != null && !searchDTO.getTags().isEmpty()) {
                searchDTO.setTagList(List.of(searchDTO.getTags().split(",")));
            }

            List<CompanionPostVO> posts = companionService.getAllPosts(searchDTO);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("목록 조회 실패");
        }
    }
    
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostDetail(@PathVariable("postId") Integer postId) {
        CompanionPostVO post = companionService.getPostDetail(postId);
        if (post != null) {
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
        }
    }
    
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable("postId") Integer postId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Integer userId = Integer.parseInt(userDetails.getUsername());

            companionService.deletePost(postId, userId);
            return ResponseEntity.ok("게시글이 삭제되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 중 오류 발생");
        }
    }
}