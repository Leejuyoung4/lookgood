package com.oo.video.model.dao;

import java.util.List;
import com.oo.video.model.dto.Video;

public interface SavedVideoDao {
    List<Video> selectSavedVideos(int userNo);
    int insertSavedVideo(int userNo, int videoNo);
    int deleteSavedVideo(int userNo, int videoNo);
    int checkSavedVideo(int userNo, int videoNo);
}