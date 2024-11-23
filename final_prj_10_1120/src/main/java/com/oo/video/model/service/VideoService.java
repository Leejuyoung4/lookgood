package com.oo.video.model.service;

import java.util.List;

import com.oo.video.model.dto.Video;

public interface VideoService {

	List<Video> getAllVideos();

	Video getVideoById(int id);

	boolean incrementViews(int id);

	List<Video> getRecommendedVideos(Long currentVideoId, String category, int limit);

	int updateLikes(int videoId);

	int updateHates(int videoId);

	void saveVideo(int userNo, int vNo);
    void unsaveVideo(int userNo, int vNo);
    boolean checkSaveStatus(int userNo, int vNo);
    List<Video> getSavedVideos(int userNo);
	
}
