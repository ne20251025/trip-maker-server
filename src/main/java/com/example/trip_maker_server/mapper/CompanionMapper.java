package com.example.trip_maker_server.mapper;

import com.example.trip_maker_server.vo.CompanionPostVO;
import com.example.trip_maker_server.vo.CompanionSearchDTO;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CompanionMapper {
    
    void insertPost(CompanionPostVO vo);
    
    CompanionPostVO selectPostById(Integer postId);

	void deletePost(Integer postId);
	
	List<CompanionPostVO> selectAllPosts(CompanionSearchDTO searchDTO);
}