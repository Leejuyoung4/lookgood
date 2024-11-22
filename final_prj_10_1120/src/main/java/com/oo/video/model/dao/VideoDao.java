package com.oo.video.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.oo.video.model.dto.Video;

public interface VideoDao {
    
    /**
     * 모든 비디오 목록을 조회합니다.
     * @return 비디오 목록
     * @throws Exception DB 조회 중 발생할 수 있는 예외
     */
    List<Video> selectAll() throws Exception;

    /**
     * 특정 ID의 비디오를 조회합니다.
     * @param id 조회할 비디오의 ID
     * @return 조회된 비디오 객체, 없으면 null
     * @throws Exception DB 조회 중 발생할 수 있는 예외
     */
    Video selectById(int id) throws Exception;

    /**
     * 특정 비디오의 조회수를 증가시킵니다.
     * @param id 조회수를 증가시킬 비디오의 ID
     * @return 업데이트된 행의 수 (성공: 1, 실패: 0)
     * @throws Exception DB 업데이트 중 발생할 수 있는 예외
     */
    int updateViews(int id) throws Exception;
    
    
    // 같은 카테고리의 추천 비디오 찾기
    List<Video> findRecommendedVideos(@Param("currentVideoId") Integer currentVideoId, 
            @Param("category") String category, 
            @Param("limit") int limit);
	
	// 인기 있는 비디오 찾기 (특정 비디오들 제외)
    List<Video> findPopularVideosExcept(@Param("currentVideoId") Integer currentVideoId, 
            @Param("excludeIds") List<Integer> excludeIds, 
            @Param("limit") int limit);
}