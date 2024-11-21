package com.oo.video.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oo.video.model.dto.Video;
import com.oo.video.model.service.VideoService;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin("*")
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public ResponseEntity<?> getAllVideos() {
        try {
            List<Video> videos = videoService.getAllVideos();
            System.out.println("Controller Response: " + videos); // 로그 추가
            if (videos != null && !videos.isEmpty()) {
                return new ResponseEntity<>(videos, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("비디오 목록이 비어있습니다.", HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            e.printStackTrace(); // 스택 트레이스 출력
            return new ResponseEntity<>("비디오 목록을 가져오는 중 오류가 발생했습니다: " + e.getMessage(), 
                                      HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}