package com.oo.groupcomment.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oo.groupcomment.model.dto.GroupComment;
import com.oo.groupcomment.model.service.GroupCommentService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/group/comment")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class GroupCommentController {
	// 서비스 의존성 주입
	private final GroupCommentService groupCommentService;

	@Autowired
	public GroupCommentController(GroupCommentService groupCommentService) {
		this.groupCommentService = groupCommentService;
	}
	
	// 해당 게시글의 댓글 목록 가져오기
	@GetMapping("{gBoardNo}")
	 public ResponseEntity<?> getCommentsBygBoardNo(@PathVariable("gBoardNo") int gBoardNo) {
        List<GroupComment> groupComments = groupCommentService.getCommentsBygBoardNo(gBoardNo);
        if (!groupComments.isEmpty()) {
            return ResponseEntity.ok(groupComments); // 댓글 목록 반환
        }
        return ResponseEntity.ok(groupComments != null ? groupComments : new ArrayList<>());
    }
	
	// 댓글 등록
	@PostMapping("/{gBoardNo}")
	public ResponseEntity<?> addComment(@PathVariable("gBoardNo") int gBoardNo, @RequestBody GroupComment groupComment) {
	    try {
	        groupComment.setgBoardNo(gBoardNo); // 게시글 번호 설정
	        groupCommentService.addComment(groupComment);
	        return ResponseEntity.status(HttpStatus.CREATED).body("댓글이 작성되었습니다.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 작성에 실패했습니다.");
	    }
	}

	// 댓글 삭제
	@DeleteMapping("{gBoardCommentNo}")
	public ResponseEntity<?> deleteComment(@PathVariable("gBoardCommentNo") int gBoardCommentNo) {
		boolean deleted = groupCommentService.deleteComment(gBoardCommentNo);
		if (deleted) {
			return ResponseEntity.ok("댓글 삭제 성공");
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	
	// 댓글 수정
	@PutMapping("{gBoardCommentNo}")
	public ResponseEntity<?> updateComment(@PathVariable("gBoardCommentNo") int gBoardCommentNo, @RequestBody GroupComment groupComment) {
		try {
			groupComment.setgBoardCommentNo(gBoardCommentNo);
			groupCommentService.updateGroupComment(groupComment);
			return ResponseEntity.ok("댓글 수정 성공");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	// 댓글 좋아요
	@PostMapping("{gBoardCommentNo}/like")
	public ResponseEntity<?> likeComment(@PathVariable int gBoardCommentNo, @RequestBody int userNo) {
	    boolean liked = groupCommentService.addLike(gBoardCommentNo, userNo);
	    if (liked) {
	        return ResponseEntity.ok("좋아요 성공");
	    }
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 좋아요를 눌렀습니다.");
	}

	

	
	
	
}
