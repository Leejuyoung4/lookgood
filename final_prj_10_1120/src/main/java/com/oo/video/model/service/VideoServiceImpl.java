package com.oo.video.model.service;

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
	

}


