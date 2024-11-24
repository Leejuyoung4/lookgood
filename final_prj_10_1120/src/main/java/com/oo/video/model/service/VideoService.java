package com.oo.video.model.service;

import java.util.List;
import com.oo.video.model.dto.Video;

public interface VideoService {
    // 기본 비디오 기능
    List<Video> getAllVideos();
    Video getVideoById(int id);
    boolean incrementViews(int id);
    int updateLikes(int videoId);
    int updateHates(int videoId);
    
    // 추천 비디오 기능
    List<Video> getRecommendedVideos(Long currentVideoId, String category, int limit);
    
    // 저장하기 기능
    void saveVideo(int userNo, int vNo);
    void unsaveVideo(int userNo, int vNo);
    boolean checkSaveStatus(int userNo, int vNo);
    List<Video> getSavedVideos(int userNo);
    
    // 플레이리스트 기능
    void updatePlaylist(int vNo, String playlistName, int userNo);
    List<Video> getVideosByPlaylist(int userNo, String playlistName);
    List<Video> getAllUserVideos(int userNo);
    List<String> getUserPlaylists(int userNo);  // 이 메서드가 누락되었었습니다
    void removeFromPlaylist(int vNo, int userNo);
}