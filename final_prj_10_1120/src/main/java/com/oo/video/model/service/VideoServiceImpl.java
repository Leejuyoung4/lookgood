package com.oo.video.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oo.video.model.dao.VideoDao;
import com.oo.video.model.dto.Video;

/**
 * 비디오 서비스 구현 클래스
 */
@Service
public class VideoServiceImpl implements VideoService {
    
    private final VideoDao videoDao;

    @Autowired
    public VideoServiceImpl(VideoDao videoDao) {
        this.videoDao = videoDao;
    }

    /**
     * 모든 비디오 목록을 조회합니다.
     * @return 전체 비디오 목록
     * @throws RuntimeException 비디오 조회 실패 시
     */
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

    /**
     * 특정 ID의 비디오를 조회하고 조회수를 증가시킵니다.
     * @param id 비디오 번호
     * @return 조회된 비디오 정보
     * @throws RuntimeException 비디오 조회 실패 시
     */
    @Override
    public Video getVideoById(int id) {
        try {
            incrementViews(id);
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

    /**
     * 비디오의 조회수를 증가시킵니다.
     * @param id 비디오 번호
     * @return 업데이트 성공 여부
     * @throws RuntimeException 조회수 증가 실패 시
     */
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
    
    /**
     * 추천 비디오 목록을 조회합니다.
     * @param currentVideoId 현재 시청 중인 비디오 ID
     * @param category 카테고리
     * @param limit 조회할 비디오 수
     * @return 추천 비디오 목록
     */
    @Override
    public List<Video> getRecommendedVideos(Long currentVideoId, String category, int limit) {
        try {
            System.out.println("currentVideoId: " + currentVideoId);
            System.out.println("category: " + category);
            System.out.println("limit: " + limit);
            
            List<Video> recommendedVideos = videoDao.findRecommendedVideos(
                currentVideoId.intValue(), 
                category, 
                limit
            );
            
            System.out.println("recommendedVideos size: " + recommendedVideos.size());
            
            if (recommendedVideos.isEmpty()) {
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
    
    /**
     * 비디오의 좋아요 수를 증가시킵니다.
     * @param videoId 비디오 번호
     * @return 업데이트된 행의 수
     */
    @Override
    public int updateLikes(int videoId) {
        return videoDao.updateLikes(videoId);
    }

    /**
     * 비디오의 싫어요 수를 증가시킵니다.
     * @param videoId 비디오 번호
     * @return 업데이트된 행의 수
     */
    @Override
    public int updateHates(int videoId) {
        return videoDao.updateHates(videoId);
    }
    
    /**
     * 사용자가 비디오를 저장합니다.
     * @param userNo 사용자 번호
     * @param vNo 비디오 번호
     * @throws RuntimeException 저장 실패 시
     */
    @Override
    public void saveVideo(int userNo, int vNo) {
        try {
            videoDao.saveVideo(userNo, vNo);
        } catch (Exception e) {
            throw new RuntimeException("영상 저장에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자가 저장한 비디오를 삭제합니다.
     * @param userNo 사용자 번호
     * @param vNo 비디오 번호
     * @throws RuntimeException 삭제 실패 시
     */
    @Override
    public void unsaveVideo(int userNo, int vNo) {
        try {
            videoDao.unsaveVideo(userNo, vNo);
        } catch (Exception e) {
            throw new RuntimeException("저장 취소에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자의 비디오 저장 상태를 확인합니다.
     * @param userNo 사용자 번호
     * @param vNo 비디오 번호
     * @return 저장 여부
     * @throws RuntimeException 상태 확인 실패 시
     */
    @Override
    public boolean checkSaveStatus(int userNo, int vNo) {
        try {
            return videoDao.checkSaveStatus(userNo, vNo);
        } catch (Exception e) {
            throw new RuntimeException("저장 상태 확인에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자가 저장한 비디오 목록을 조회합니다.
     * @param userNo 사용자 번호
     * @return 저장된 비디오 목록
     * @throws RuntimeException 조회 실패 시
     */
    @Override
    public List<Video> getSavedVideos(int userNo) {
        try {
            return videoDao.getSavedVideos(userNo);
        } catch (Exception e) {
            throw new RuntimeException("저장된 영상 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 비디오의 플레이리스트 정보를 업데이트합니다.
     * @param vNo 비디오 번호
     * @param playlistName 플레이리스트 이름
     * @param userNo 사용자 번호
     * @throws RuntimeException 업데이트 실패 시
     */
    @Override
    public void updatePlaylist(int vNo, String playlistName, int userNo) {
        try {
            videoDao.updatePlaylist(vNo, playlistName, userNo);
        } catch (Exception e) {
            throw new RuntimeException("플레이리스트 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 플레이리스트의 비디오 목록을 조회합니다.
     * @param userNo 사용자 번호
     * @param playlistName 플레이리스트 이름
     * @return 플레이리스트에 포함된 비디오 목록
     * @throws RuntimeException 조회 실패 시
     */
    @Override
    public List<Video> getVideosByPlaylist(int userNo, String playlistName) {
        try {
            return videoDao.getVideosByPlaylist(userNo, playlistName);
        } catch (Exception e) {
            throw new RuntimeException("플레이리스트 비디오 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자의 모든 비디오를 조회합니다.
     * @param userNo 사용자 번호
     * @return 사용자의 모든 비디오 목록
     * @throws RuntimeException 조회 실패 시
     */
    @Override
    public List<Video> getAllUserVideos(int userNo) {
        try {
            return videoDao.getAllUserVideos(userNo);
        } catch (Exception e) {
            throw new RuntimeException("사용자 비디오 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자의 플레이리스트 목록을 조회합니다.
     * @param userNo 사용자 번호
     * @return 플레이리스트 이름 목록
     * @throws RuntimeException 조회 실패 시
     */
    @Override
    public List<String> getUserPlaylists(int userNo) {
        try {
            List<String> playlists = videoDao.getUserPlaylists(userNo);
            System.out.println("플레이리스트 조회 결과: " + playlists); // 디버깅용
            return playlists != null ? playlists : new ArrayList<>();
            
        } catch (Exception e) {
            System.err.println("플레이리스트 조회 실패 - userNo: " + userNo);
            e.printStackTrace();
            throw new RuntimeException("플레이리스트 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 비디오를 플레이리스트에서 제거합니다.
     * @param vNo 비디오 번호
     * @param userNo 사용자 번호
     * @throws RuntimeException 제거 실패 시
     */
    @Override
    public void removeFromPlaylist(int vNo, int userNo) {
        try {
            videoDao.removeFromPlaylist(vNo, userNo);
        } catch (Exception e) {
            throw new RuntimeException("플레이리스트에서 비디오 제거에 실패했습니다: " + e.getMessage());
        }
    }
}