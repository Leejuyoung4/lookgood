package com.oo.video.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oo.video.model.dao.VideoDao;
import com.oo.video.model.dto.Video;

@Service
public class VideoServiceImpl implements VideoService {
    
    private final VideoDao videoDao;

    @Autowired
    public VideoServiceImpl(VideoDao videoDao) {
        this.videoDao = videoDao;
    }

    @Override
    public List<Video> getAllVideos() {
        try {
            List<Video> videos = videoDao.selectAll();
            if (videos == null) {
                throw new Exception("비디오 데이터를 찾을 수 없습니다.");
            }
            return videos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("비디오 목록을 가져오는데 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public Video getVideoById(int id) {
        try {
            // 먼저 조회수 증가 메서드 호출
            incrementViews(id);
            
            // 그 다음 비디오 정보 조회
            Video video = videoDao.selectById(id);
            if (video == null) {
                throw new Exception("해당 ID의 비디오를 찾을 수 없습니다: " + id);
            }
            return video;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("비디오를 가져오는데 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public boolean incrementViews(int id) {
        try {
            int result = videoDao.updateViews(id);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("조회수 증가에 실패했습니다: " + e.getMessage());
        }
    }
    
    @Override
    public List<Video> getRecommendedVideos(Long currentVideoId, String category, int limit) {
        try {
            // 디버깅을 위한 로그 추가
            System.out.println("currentVideoId: " + currentVideoId);
            System.out.println("category: " + category);
            System.out.println("limit: " + limit);
            
            List<Video> recommendedVideos = videoDao.findRecommendedVideos(
                currentVideoId.intValue(), 
                category, 
                limit
            );
            
            // 결과 확인을 위한 로그
            System.out.println("recommendedVideos size: " + recommendedVideos.size());
            
            if (recommendedVideos.isEmpty()) {
                // 같은 카테고리 영상이 없으면 인기 영상 가져오기
                recommendedVideos = videoDao.findPopularVideosExcept(
                    currentVideoId.intValue(),
                    new ArrayList<>(),
                    limit
                );
            }
            
            return recommendedVideos;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public int updateLikes(int videoId) {
        return videoDao.updateLikes(videoId);
    }

    @Override
    public int updateHates(int videoId) {
        return videoDao.updateHates(videoId);
    }
    
 // VideoServiceImpl에 추가할 메서드들
    @Override
    public void saveVideo(int userNo, int vNo) {
        try {
            videoDao.saveVideo(userNo, vNo);
        } catch (Exception e) {
            throw new RuntimeException("영상 저장에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public void unsaveVideo(int userNo, int vNo) {
        try {
            videoDao.unsaveVideo(userNo, vNo);
        } catch (Exception e) {
            throw new RuntimeException("저장 취소에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public boolean checkSaveStatus(int userNo, int vNo) {
        try {
            return videoDao.checkSaveStatus(userNo, vNo);
        } catch (Exception e) {
            throw new RuntimeException("저장 상태 확인에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public List<Video> getSavedVideos(int userNo) {
        try {
            return videoDao.getSavedVideos(userNo);
        } catch (Exception e) {
            throw new RuntimeException("저장된 영상 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    

    
}


