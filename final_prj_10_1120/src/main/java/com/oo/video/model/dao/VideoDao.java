package com.oo.video.model.dao;

import java.util.List;

import com.oo.video.model.dto.Video;

public interface VideoDao {

	
	/**
     * 모든 비디오 목록을 조회합니다.
     * @return 비디오 목록
     * @throws Exception DB 조회 중 발생할 수 있는 예외
     */
    List<Video> selectAll() throws Exception;

}
