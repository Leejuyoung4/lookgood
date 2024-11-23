package com.oo.video.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.oo.video.model.dto.Video;
import com.oo.video.model.service.SavedVideoService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/saved-videos")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class SavedVideoController {

    private final SavedVideoService savedVideoService;

    public SavedVideoController(SavedVideoService savedVideoService) {
        this.savedVideoService = savedVideoService;
    }

    /**
     * 저장된 영상 목록 조회
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getSavedVideos(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer userNo = (Integer) session.getAttribute("userNo");
            if (userNo == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            List<Video> savedVideos = savedVideoService.getSavedVideos(userNo);
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
     * 영상 저장하기
     */
    @PostMapping("/{videoNo}")
    public ResponseEntity<Map<String, Object>> saveVideo(
            @PathVariable int videoNo,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer userNo = (Integer) session.getAttribute("userNo");
            if (userNo == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 이미 저장된 영상인지 확인
            if (savedVideoService.isVideoSaved(userNo, videoNo)) {
                response.put("success", false);
                response.put("message", "이미 저장된 영상입니다.");
                return ResponseEntity.badRequest().body(response);
            }

            boolean result = savedVideoService.saveVideo(userNo, videoNo);
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
     * 저장된 영상 삭제
     */
    @DeleteMapping("/{videoNo}")
    public ResponseEntity<Map<String, Object>> unsaveVideo(
            @PathVariable int videoNo,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer userNo = (Integer) session.getAttribute("userNo");
            if (userNo == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            boolean result = savedVideoService.unsaveVideo(userNo, videoNo);
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
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer userNo = (Integer) session.getAttribute("userNo");
            if (userNo == null) {
                response.put("success", true);
                response.put("isSaved", false);
                return ResponseEntity.ok(response);
            }

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
}