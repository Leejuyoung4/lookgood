package com.oo.video.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oo.video.model.dto.Video;
import com.oo.video.model.service.VideoService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "*", allowCredentials = "false")
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
    
    @PutMapping("/{videoId}/like")
    public ResponseEntity<?> updateLikes(@PathVariable int videoId) {
        try {
            int result = videoService.updateLikes(videoId);
            if (result > 0) {
                return new ResponseEntity<>("좋아요가 반영되었습니다.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("비디오를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("좋아요 업데이트 실패: " + e.getMessage(), 
                                      HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{videoId}/hate")
    public ResponseEntity<?> updateHates(@PathVariable int videoId) {
        try {
            int result = videoService.updateHates(videoId);
            if (result > 0) {
                return new ResponseEntity<>("싫어요가 반영되었습니다.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("비디오를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("싫어요 업데이트 실패: " + e.getMessage(), 
                                      HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
 // VideoController에 추가할 메서드들

    @PostMapping("/{vno}/save")
    public ResponseEntity<?> saveVideo(@PathVariable int vno, HttpSession session) {
        try {
            // 세션에서 사용자 정보 가져오기
            Integer userNo = (Integer) session.getAttribute("userNo");
            if (userNo == null) {
                return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }
            
            videoService.saveVideo(userNo, vno);
            return new ResponseEntity<>("영상이 저장되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("영상 저장 실패: " + e.getMessage(), 
                                      HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{vno}/save")
    public ResponseEntity<?> unsaveVideo(@PathVariable int vno, HttpSession session) {
        try {
            Integer userNo = (Integer) session.getAttribute("userNo");
            if (userNo == null) {
                return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }
            
            videoService.unsaveVideo(userNo, vno);
            return new ResponseEntity<>("저장이 취소되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("저장 취소 실패: " + e.getMessage(), 
                                      HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{vno}/save/check")
    public ResponseEntity<?> checkSaveStatus(@PathVariable int vno, HttpSession session) {
        try {
            Integer userNo = (Integer) session.getAttribute("userNo");
            if (userNo == null) {
                return ResponseEntity.ok(false);
            }
            
            boolean isSaved = videoService.checkSaveStatus(userNo, vno);
            return ResponseEntity.ok(isSaved);
        } catch (Exception e) {
            return new ResponseEntity<>("저장 상태 확인 실패: " + e.getMessage(), 
                                      HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/saved")
    public ResponseEntity<?> getSavedVideos(HttpSession session) {
        try {
            Integer userNo = (Integer) session.getAttribute("userNo");
            if (userNo == null) {
                return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }
            
            List<Video> savedVideos = videoService.getSavedVideos(userNo);
            return ResponseEntity.ok(savedVideos);
        } catch (Exception e) {
            return new ResponseEntity<>("저장된 영상 조회 실패: " + e.getMessage(), 
                                      HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    

}
