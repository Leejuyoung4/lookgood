package com.oo.video.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.oo.video.model.dto.SavedVideo;
import com.oo.video.model.dto.Video;
import com.oo.video.model.service.SavedVideoService;

@RestController
@RequestMapping("/api/saved-videos")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class SavedVideoController {

    private final SavedVideoService savedVideoService;

    public SavedVideoController(SavedVideoService savedVideoService) {
        this.savedVideoService = savedVideoService;
    }

    /**
     * 영상 저장하기
     */
    @PostMapping("/{videoNo}")
    public ResponseEntity<Map<String, Object>> saveVideo(
            @PathVariable int videoNo,
            @RequestParam int userNo) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (savedVideoService.isVideoSaved(userNo, videoNo)) {
                response.put("success", false);
                response.put("message", "이미 저장된 영상입니다.");
                return ResponseEntity.badRequest().body(response);
            }

            SavedVideo savedVideo = new SavedVideo();
            savedVideo.setUserNo(userNo);
            savedVideo.setVNo(videoNo);
            
            boolean result = savedVideoService.saveVideo(savedVideo);
            if (result) {
                response.put("success", true);
                response.put("message", "영상이 저장되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "영상 저장에 실패했습니다.");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "영상 저장 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 진도율 업데이트
     */
    @PutMapping("/{videoNo}/progress")
    public ResponseEntity<Map<String, Object>> updateProgress(
            @PathVariable int videoNo,
            @RequestParam int userNo,
            @RequestParam int progressRate) {
        Map<String, Object> response = new HashMap<>();
        try {
            SavedVideo savedVideo = new SavedVideo();
            savedVideo.setUserNo(userNo);
            savedVideo.setVNo(videoNo);
            savedVideo.setProgressRate(progressRate);
            savedVideo.setCompleted(progressRate >= 100);

            boolean result = savedVideoService.updateProgress(savedVideo);
            if (result) {
                response.put("success", true);
                response.put("message", "진도율이 업데이트되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "진도율 업데이트에 실패했습니다.");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "진도율 업데이트 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 저장된 영상 목록 조회
     */
    @GetMapping("/user/{userNo}")
    public ResponseEntity<Map<String, Object>> getSavedVideos(@PathVariable int userNo) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<SavedVideo> savedVideos = savedVideoService.getSavedVideos(userNo);
            response.put("success", true);
            response.put("videos", savedVideos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "저장된 영상 목록 조회 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 저장된 영상 삭제
     */
    @DeleteMapping("/{videoNo}")
    public ResponseEntity<Map<String, Object>> unsaveVideo(
            @PathVariable int videoNo,
            @RequestParam int userNo) {
        Map<String, Object> response = new HashMap<>();
        try {
            SavedVideo savedVideo = new SavedVideo();
            savedVideo.setUserNo(userNo);
            savedVideo.setVNo(videoNo);

            boolean result = savedVideoService.unsaveVideo(savedVideo);
            if (result) {
                response.put("success", true);
                response.put("message", "저장된 영상이 삭제되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "영상 삭제에 실패했습니다.");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "영상 삭제 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 영상 저장 여부 확인
     */
    @GetMapping("/{videoNo}/check")
    public ResponseEntity<Map<String, Object>> checkSavedVideo(
            @PathVariable int videoNo,
            @RequestParam int userNo) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isSaved = savedVideoService.isVideoSaved(userNo, videoNo);
            response.put("success", true);
            response.put("isSaved", isSaved);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "저장 상태 확인 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 영상 완료 처리
     */
    @PutMapping("/{videoNo}/complete")
    public ResponseEntity<Map<String, Object>> completeVideo(
            @PathVariable int videoNo,
            @RequestParam int userNo) {
        Map<String, Object> response = new HashMap<>();
        try {
            SavedVideo savedVideo = new SavedVideo();
            savedVideo.setUserNo(userNo);
            savedVideo.setVNo(videoNo);
            savedVideo.setProgressRate(100);
            savedVideo.setCompleted(true);  // completed 대신 isCompleted 사용

            boolean result = savedVideoService.updateProgress(savedVideo);
            if (result) {
                response.put("success", true);
                response.put("message", "영상이 완료 처리되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "영상 완료 처리에 실패했습니다.");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "영상 완료 처리 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}