package com.oo.video.model.service;


import java.util.List;
import com.oo.video.model.dto.SavedVideo;

public interface SavedVideoService {
    /**
     * 사용자가 저장한 영상 목록을 조회합니다.
     * @param userNo 사용자 번호
     * @return 저장된 영상 목록
     */
    List<SavedVideo> getSavedVideos(int userNo);

    /**
     * 영상을 저장합니다.
     * @param savedVideo 저장할 영상 정보 (사용자 번호, 영상 번호 포함)
     * @return 저장 성공 여부
     */
    boolean saveVideo(SavedVideo savedVideo);

    /**
     * 저장된 영상을 삭제합니다.
     * @param savedVideo 삭제할 영상 정보 (사용자 번호, 영상 번호 포함)
     * @return 삭제 성공 여부
     */
    boolean unsaveVideo(SavedVideo savedVideo);

    /**
     * 영상의 저장 여부를 확인합니다.
     * @param userNo 사용자 번호
     * @param videoNo 영상 번호
     * @return 저장 여부
     */
    boolean isVideoSaved(int userNo, int videoNo);

    /**
     * 영상의 진도율을 업데이트합니다.
     * @param savedVideo 업데이트할 영상 정보 (사용자 번호, 영상 번호, 진도율 포함)
     * @return 업데이트 성공 여부
     */
    boolean updateProgress(SavedVideo savedVideo);

    /**
     * 완료된 영상 목록을 조회합니다.
     * @param userNo 사용자 번호
     * @return 완료된 영상 목록
     */
    List<SavedVideo> getCompletedVideos(int userNo);

    /**
     * 진행 중인 영상 목록을 조회합니다.
     * @param userNo 사용자 번호
     * @return 진행 중인 영상 목록
     */
    List<SavedVideo> getInProgressVideos(int userNo);
    
 // 완료 처리를 위한 새로운 메서드
    public boolean completeVideo(SavedVideo savedVideo);
    
}
