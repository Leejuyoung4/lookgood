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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oo.video.model.dto.VideoMemo;
import com.oo.video.model.service.VideoMemoService;

@RestController
@RequestMapping("/api/memos")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class VideoMemoController {

	private final VideoMemoService videoMemoService;

	public VideoMemoController(VideoMemoService videoMemoService) {
		this.videoMemoService = videoMemoService;
	}

	/**
	 * 메모 저장
	 */
	@PostMapping
	public ResponseEntity<Map<String, Object>> saveMemo(@RequestBody VideoMemo memo) {
		Map<String, Object> response = new HashMap<>();
		try {
			boolean result = videoMemoService.saveMemo(memo);
			if (result) {
				response.put("success", true);
				response.put("message", "메모가 저장되었습니다.");
				response.put("memo", memo);
				return ResponseEntity.ok(response);
			} else {
				response.put("success", false);
				response.put("message", "메모 저장에 실패했습니다.");
				return ResponseEntity.badRequest().body(response);
			}
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "메모 저장 중 오류가 발생했습니다.");
			return ResponseEntity.internalServerError().body(response);
		}
	}

	/**
	 * 비디오의 메모 목록 조회
	 */
	// VideoMemoController.java
	// 메모 목록 조회
	@GetMapping
	public ResponseEntity<?> getMemosByVideo(
	    @RequestParam(value = "vNo", required = true) int vNo,
	    @RequestParam(value = "userNo", required = true) int userNo
	) {
	    try {
	        // 디버깅을 위한 로그 추가
	        System.out.println("Received request - vNo: " + vNo + ", userNo: " + userNo);
	        
	        List<VideoMemo> memos = videoMemoService.getMemosByVideo(vNo, userNo);
	        
	        // 결과 로깅
	        System.out.println("Found memos: " + memos);
	        
	        return ResponseEntity.ok(memos);
	    } catch (Exception e) {
	        // 상세한 에러 로깅
	        System.err.println("Error in getMemosByVideo: " + e.getMessage());
	        e.printStackTrace();
	        return ResponseEntity.badRequest()
	            .body(Map.of(
	                "error", "Failed to fetch memos",
	                "message", e.getMessage()
	            ));
	    }
	}
	/**
	 * 메모 삭제
	 */
	@DeleteMapping("/{memoId}")
	public ResponseEntity<Map<String, Object>> deleteMemo(@PathVariable int memoId, @RequestParam int userNo) {
		Map<String, Object> response = new HashMap<>();
		try {
			boolean result = videoMemoService.deleteMemo(memoId, userNo);
			if (result) {
				response.put("success", true);
				response.put("message", "메모가 삭제되었습니다.");
				return ResponseEntity.ok(response);
			} else {
				response.put("success", false);
				response.put("message", "메모 삭제에 실패했습니다.");
				return ResponseEntity.badRequest().body(response);
			}
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "메모 삭제 중 오류가 발생했습니다.");
			return ResponseEntity.internalServerError().body(response);
		}
	}

	/**
	 * 메모 수정
	 */
	@PutMapping("/{memoId}")
	public ResponseEntity<Map<String, Object>> updateMemo(@PathVariable int memoId, @RequestBody VideoMemo memo) {
		Map<String, Object> response = new HashMap<>();
		try {
			memo.setMemoId(memoId);
			boolean result = videoMemoService.updateMemo(memo);
			if (result) {
				response.put("success", true);
				response.put("message", "메모가 수정되었습니다.");
				response.put("memo", memo);
				return ResponseEntity.ok(response);
			} else {
				response.put("success", false);
				response.put("message", "메모 수정에 실패했습니다.");
				return ResponseEntity.badRequest().body(response);
			}
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "메모 수정 중 오류가 발생했습니다.");
			return ResponseEntity.internalServerError().body(response);
		}
	}
}