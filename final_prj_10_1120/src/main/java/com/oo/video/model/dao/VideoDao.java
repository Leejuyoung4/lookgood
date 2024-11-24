package com.oo.video.model.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.oo.video.model.dto.Video;

/**
 * 비디오 데이터 접근을 위한 DAO 인터페이스
 */
public interface VideoDao {
    // 기본 비디오 조회/수정 기능
    List<Video> selectAll() throws Exception;
    Video selectById(int id) throws Exception;
    int updateViews(int id) throws Exception;
    int updateLikes(int videoId);
    int updateHates(int videoId);
    
    // 추천 비디오 관련
    List<Video> findRecommendedVideos(@Param("currentVideoId") Integer currentVideoId, 
                                     @Param("category") String category, 
                                     @Param("limit") int limit);
    
    List<Video> findPopularVideosExcept(@Param("currentVideoId") Integer currentVideoId, 
                                       @Param("excludeIds") List<Integer> excludeIds, 
                                       @Param("limit") int limit);
    
    // 비디오 저장 관련
    void saveVideo(@Param("userNo") int userNo, @Param("vNo") int vNo);
    void unsaveVideo(@Param("userNo") int userNo, @Param("vNo") int vNo);
    boolean checkSaveStatus(@Param("userNo") int userNo, @Param("vNo") int vNo);
    List<Video> getSavedVideos(int userNo);
    
    // 플레이리스트 관련
    void updatePlaylist(@Param("vNo") int vNo, 
                       @Param("playlistName") String playlistName, 
                       @Param("userNo") int userNo);
    
    List<Video> getVideosByPlaylist(@Param("userNo") int userNo, 
                                   @Param("playlistName") String playlistName);
    
    List<Video> getAllUserVideos(int userNo);
    /**
     * 사용자의 플레이리스트 목록을 조회합니다.
     * @param userNo 사용자 번호
     * @return 플레이리스트 이름 목록
     */
    List<String> getUserPlaylists(int userNo);
    void removeFromPlaylist(@Param("vNo") int vNo, @Param("userNo") int userNo);
 
}