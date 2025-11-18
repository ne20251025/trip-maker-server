package com.example.trip_maker_server.vo;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CompanionSearchDTO {
    private String searchType; // "mix"(제목+내용), "title", "content"
    private String keyword;
    
    private String tags;       // 프론트에서 보내는 원본 문자열 ("20대,맛집")
    private List<String> tagList; // (★신규★) 쪼개서 담을 리스트
}