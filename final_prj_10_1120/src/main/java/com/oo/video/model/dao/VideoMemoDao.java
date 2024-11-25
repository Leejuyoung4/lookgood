package com.oo.video.model.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.oo.video.model.dto.VideoMemo;

@Mapper
public interface VideoMemoDao {
    // 메모 저장
    int insertMemo(VideoMemo memo);
    
    // 비디오의 메모 목록 조회
    List<VideoMemo> selectMemosByVideo(@Param("vNo") int vNo, @Param("userNo") int userNo);
    
    // 특정 메모 조회
    VideoMemo selectMemoById(int memoId);
    
    // 메모 삭제
    int deleteMemo(int memoId);
    
    // 메모 수정
    int updateMemo(VideoMemo memo);
}