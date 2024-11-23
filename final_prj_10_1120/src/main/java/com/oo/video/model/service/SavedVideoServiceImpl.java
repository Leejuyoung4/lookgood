package com.oo.video.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oo.video.model.dao.SavedVideoDao;
import com.oo.video.model.dto.Video;

@Service
@Transactional
public class SavedVideoServiceImpl implements SavedVideoService {

    private final SavedVideoDao savedVideoDao;

    public SavedVideoServiceImpl(SavedVideoDao savedVideoDao) {
        this.savedVideoDao = savedVideoDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Video> getSavedVideos(int userNo) {
        try {
            return savedVideoDao.selectSavedVideos(userNo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("저장된 영상 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public boolean saveVideo(int userNo, int videoNo) {
        try {
            // 이미 저장된 영상인지 확인
            if (isVideoSaved(userNo, videoNo)) {
                return false;
            }
            return savedVideoDao.insertSavedVideo(userNo, videoNo) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("영상 저장 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public boolean unsaveVideo(int userNo, int videoNo) {
        try {
            return savedVideoDao.deleteSavedVideo(userNo, videoNo) > 0;
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
}