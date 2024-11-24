package com.oo.video.model.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.oo.video.model.dto.SavedVideo;

@Mapper
public interface SavedVideoDao {
    /**
     * 사용자의 저장된 모든 영상 목록을 조회합니다.
     * @param userNo 사용자 번호
     * @return 저장된 영상 목록
     */
    List<SavedVideo> selectSavedVideos(int userNo);

    /**
     * 새로운 영상을 저장합니다.
     * @param savedVideo 저장할 영상 정보
     * @return 영향받은 행의 수
     */
    int insertSavedVideo(SavedVideo savedVideo);

    /**
     * 저장된 영상을 삭제합니다.
     * @param savedVideo 삭제할 영상 정보
     * @return 영향받은 행의 수
     */
    int deleteSavedVideo(SavedVideo savedVideo);

    /**
     * 영상의 저장 여부를 확인합니다.
     * @param userNo 사용자 번호
     * @param videoNo 영상 번호
     * @return 저장된 영상의 수
     */
    int checkSavedVideo(int userNo, int videoNo);

    /**
     * 영상의 진도율을 업데이트합니다.
     * @param savedVideo 업데이트할 영상 정보
     * @return 영향받은 행의 수
     */
    int updateProgress(SavedVideo savedVideo);

    /**
     * 완료된(100%) 영상 목록을 조회합니다.
     * @param userNo 사용자 번호
     * @return 완료된 영상 목록
     */
    List<SavedVideo> selectCompletedVideos(int userNo);

    /**
     * 진행 중인(100% 미만) 영상 목록을 조회합니다.
     * @param userNo 사용자 번호
     * @return 진행 중인 영상 목록
     */
    List<SavedVideo> selectInProgressVideos(int userNo);
}