package com.oo.video.model.dao;

import java.util.List;
import com.oo.video.model.dto.Video;

public interface SavedVideoDao {
    /**
     * 사용자가 저장한 영상 목록을 조회합니다.
     */
    List<Video> selectSavedVideos(int userNo);

    /**
     * 영상을 저장합니다.
     */
    int insertSavedVideo(int userNo, int videoNo);

    /**
     * 저장된 영상을 삭제합니다.
     */
    int deleteSavedVideo(int userNo, int videoNo);

    /**
     * 영상의 저장 여부를 확인합니다.
     */
    int checkSavedVideo(int userNo, int videoNo);
}