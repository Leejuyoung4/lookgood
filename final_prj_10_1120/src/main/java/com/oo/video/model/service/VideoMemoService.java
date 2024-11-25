package com.oo.video.model.service;

import java.util.List;
import com.oo.video.model.dto.VideoMemo;

public interface VideoMemoService {
    // 메모 저장
    boolean saveMemo(VideoMemo memo);
    
    // 비디오의 메모 목록 조회
    List<VideoMemo> getMemosByVideo(int vNo, int userNo);
    
    // 메모 삭제
    boolean deleteMemo(int memoId, int userNo);
    
    // 메모 수정
    boolean updateMemo(VideoMemo memo);
}