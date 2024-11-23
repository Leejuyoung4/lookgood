package com.oo.video.model.service;

import java.util.List;
import com.oo.video.model.dto.Video;

public interface SavedVideoService {
    /**
     * 사용자가 저장한 영상 목록을 조회합니다.
     * @param userNo 사용자 번호
     * @return 저장된 영상 목록
     */
    List<Video> getSavedVideos(int userNo);

    /**
     * 영상을 저장합니다.
     * @param userNo 사용자 번호
     * @param videoNo 영상 번호
     * @return 저장 성공 여부
     */
    boolean saveVideo(int userNo, int videoNo);

    /**
     * 저장된 영상을 삭제합니다.
     * @param userNo 사용자 번호
     * @param videoNo 영상 번호
     * @return 삭제 성공 여부
     */
    boolean unsaveVideo(int userNo, int videoNo);

    /**
     * 영상의 저장 여부를 확인합니다.
     * @param userNo 사용자 번호
     * @param videoNo 영상 번호
     * @return 저장 여부
     */
    boolean isVideoSaved(int userNo, int videoNo);
}

