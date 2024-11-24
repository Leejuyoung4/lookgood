package com.oo.video.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oo.video.model.dao.SavedVideoDao;
import com.oo.video.model.dto.SavedVideo;

@Service
@Transactional
public class SavedVideoServiceImpl implements SavedVideoService {

    private final SavedVideoDao savedVideoDao;

    public SavedVideoServiceImpl(SavedVideoDao savedVideoDao) {
        this.savedVideoDao = savedVideoDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavedVideo> getSavedVideos(int userNo) {
        try {
            return savedVideoDao.selectSavedVideos(userNo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("저장된 영상 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public boolean saveVideo(SavedVideo savedVideo) {
        try {
            if (isVideoSaved(savedVideo.getUserNo(), savedVideo.getVNo())) {
                return false;
            }
            return savedVideoDao.insertSavedVideo(savedVideo) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("영상 저장 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public boolean unsaveVideo(SavedVideo savedVideo) {
        try {
            return savedVideoDao.deleteSavedVideo(savedVideo) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("영상 삭제 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVideoSaved(int userNo, int videoNo) {
        try {
            return savedVideoDao.checkSavedVideo(userNo, videoNo) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("저장 상태 확인 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public boolean updateProgress(SavedVideo savedVideo) {
        try {
            return savedVideoDao.updateProgress(savedVideo) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("진도율 업데이트 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavedVideo> getCompletedVideos(int userNo) {
        try {
            return savedVideoDao.selectCompletedVideos(userNo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("완료된 영상 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavedVideo> getInProgressVideos(int userNo) {
        try {
            return savedVideoDao.selectInProgressVideos(userNo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("진행 중인 영상 목록 조회 중 오류가 발생했습니다.", e);
        }
    }
    
    @Override
    public boolean completeVideo(SavedVideo savedVideo) {
        try {
            savedVideo.setProgressRate(100);
            savedVideo.setCompleted(true);  // completed 대신 isCompleted 사용
            return savedVideoDao.updateProgress(savedVideo) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}