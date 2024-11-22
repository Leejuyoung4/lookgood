package com.oo.inquirycomment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oo.groupcomment.model.dto.GroupComment;
import com.oo.inquirycomment.model.dto.InquiryComment;
import com.oo.inquirycomment.model.service.InquiryCommentService;

@RestController
@RequestMapping("/api/")
@CrossOrigin(origins = "*")
public class InquiryCommentController {
	// 서비스 의존성 주입
	private final InquiryCommentService inquiryCommentService;

	public InquiryCommentController(InquiryCommentService inquiryCommentService) {
		this.inquiryCommentService = inquiryCommentService;
	}
	
	// 해당 게시글의 댓글 목록 가져오기
		@GetMapping("{iNo}")
		 public ResponseEntity<?> getCommentsBygINo(@PathVariable("iNo") int iNo) {
	        List<InquiryComment> inquiryComments = inquiryCommentService.getCommentsBygINo(iNo);
	        if (!inquiryComments.isEmpty()) {
	            return ResponseEntity.ok(inquiryComments); // 댓글 목록 반환
	        }
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글이 존재하지 않습니다."); // 에러 메시지 반환
	    }
		
//		// 댓글 삭제
//		@DeleteMapping("{gBoardCommentNo}")
//		public ResponseEntity<?> deleteComment(@PathVariable("gBoardCommentNo") int gBoardCommentNo) {
//			boolean deleted = groupCommentService.deleteComment(gBoardCommentNo);
//			if (deleted) {
//				return ResponseEntity.ok("댓글 삭제 성공");
//			}
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//		}
//		
//		// 댓글 수정
//		@PutMapping("{gBoardCommentNo}")
//		public ResponseEntity<?> updateComment(@PathVariable("gBoardCommentNo") int gBoardCommentNo, @RequestBody GroupComment groupComment) {
//			try {
//				groupComment.setgBoardCommentNo(gBoardCommentNo);
//				groupCommentService.updateGroupComment(groupComment);
//				return ResponseEntity.ok("댓글 수정 성공");
//			} catch (Exception e) {
//				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//			}
//		}
}
