package com.oo.video.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            System.out.println("Controller Response: " + videos);
            if (videos != null && !videos.isEmpty()) {
                return new ResponseEntity<>(videos, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("비디오 목록이 비어있습니다.", HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("비디오 목록을 가져오는 중 오류가 발생했습니다: " + e.getMessage(), 
                                      HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 특정 ID의 비디오를 가져오는 엔드포인트 추가
    @GetMapping("/{id}")
    public ResponseEntity<?> getVideoById(@PathVariable int id) {
        try {
            Video video = videoService.getVideoById(id);
            if (video != null) {
                System.out.println("Found video: " + video); // 로그 추가
                return new ResponseEntity<>(video, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("해당 ID의 비디오를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("비디오를 가져오는 중 오류가 발생했습니다: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 조회수 증가 엔드포인트 추가 (선택사항)
    @PutMapping("/{id}/views")
    public ResponseEntity<?> incrementViews(@PathVariable int id) {
        try {
            boolean updated = videoService.incrementViews(id);
            if (updated) {
                return new ResponseEntity<>("조회수가 증가되었습니다.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("해당 ID의 비디오를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("조회수 증가 중 오류가 발생했습니다: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/recommended")
    public ResponseEntity<List<Video>> getRecommendedVideos(
        @RequestParam(name = "currentVideoId") Integer currentVideoId,
        @RequestParam(required = false) String category,
        @RequestParam(defaultValue = "5") int limit
    ) {
        try {
            // 디버깅을 위한 로그 추가
            System.out.println("Controller - currentVideoId: " + currentVideoId);
            System.out.println("Controller - category: " + category);
            System.out.println("Controller - limit: " + limit);
            
            List<Video> recommendedVideos = videoService.getRecommendedVideos(
                currentVideoId.longValue(), 
                category, 
                limit
            );
            
            // 결과 확인을 위한 로그
            System.out.println("Controller - result size: " + recommendedVideos.size());
            
            return ResponseEntity.ok(recommendedVideos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
