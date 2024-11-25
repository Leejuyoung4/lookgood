package com.oo.video.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oo.video.model.dao.VideoMemoDao;
import com.oo.video.model.dto.VideoMemo;

@Service
public class VideoMemoServiceImpl implements VideoMemoService {

    private final VideoMemoDao videoMemoDao;

    public VideoMemoServiceImpl(VideoMemoDao videoMemoDao) {
        this.videoMemoDao = videoMemoDao;
    }

    @Override
    @Transactional
    public boolean saveMemo(VideoMemo memo) {
        try {
            // 필수 필드 검증
            if (memo.getUserNo() <= 0 || memo.getvNo() <= 0 || 
                memo.getContent() == null || memo.getContent().trim().isEmpty()) {
                return false;
            }
            
            return videoMemoDao.insertMemo(memo) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<VideoMemo> getMemosByVideo(int vNo, int userNo) {
        try {
            // 유효성 검사
            if (vNo <= 0 || userNo <= 0) {
                return null;
            }
            
            return videoMemoDao.selectMemosByVideo(vNo, userNo);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional
    public boolean deleteMemo(int memoId, int userNo) {
        try {
            // 유효성 검사
            if (memoId <= 0 || userNo <= 0) {
                return false;
            }
            
            // 메모 소유자 확인
            VideoMemo existingMemo = videoMemoDao.selectMemoById(memoId);
            if (existingMemo == null || existingMemo.getUserNo() != userNo) {
                return false;
            }
            
            return videoMemoDao.deleteMemo(memoId) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateMemo(VideoMemo memo) {
        try {
            // 필수 필드 검증
            if (memo.getMemoId() <= 0 || memo.getUserNo() <= 0 || 
                memo.getContent() == null || memo.getContent().trim().isEmpty()) {
                return false;
            }
            
            // 메모 소유자 확인
            VideoMemo existingMemo = videoMemoDao.selectMemoById(memo.getMemoId());
            if (existingMemo == null || existingMemo.getUserNo() != memo.getUserNo()) {
                return false;
            }
            
            return videoMemoDao.updateMemo(memo) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}