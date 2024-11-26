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
    private final GroupCommentService groupCommentService;

    @Autowired
    public GroupCommentController(GroupCommentService groupCommentService) {
        this.groupCommentService = groupCommentService;
    }
    
    // 해당 게시글의 댓글 목록 가져오기
    @GetMapping("{boardNo}")
    public ResponseEntity<List<GroupComment>> getCommentsByBoardNo(@PathVariable("boardNo") int boardNo) {
        List<GroupComment> comments = groupCommentService.getCommentsByBoardNo(boardNo);
        return ResponseEntity.ok(comments != null ? comments : new ArrayList<>());
    }
    
    @PostMapping("/{boardNo}")
    public ResponseEntity<String> addComment(
            @PathVariable("boardNo") int boardNo, 
            @RequestBody GroupComment comment) {
        try {
            System.out.println("=== 댓글 등록 시도 ===");
            System.out.println("boardNo: " + boardNo);
            System.out.println("comment: " + comment.toString());
            
            comment.setBoardNo(boardNo);
            groupCommentService.addComment(comment);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                               .body("댓글이 작성되었습니다.");
        } catch (Exception e) {
            System.err.println("=== 댓글 등록 실패 ===");
            System.err.println("에러 메시지: " + e.getMessage());
            e.printStackTrace();  // 스택 트레이스 출력
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("댓글 작성에 실패했습니다. 원인: " + e.getMessage());
        }
    }    
    
    
    

    // 댓글 삭제
    @DeleteMapping("{boardCommentNo}")
    public ResponseEntity<String> deleteComment(@PathVariable("boardCommentNo") int boardCommentNo) {
        return groupCommentService.deleteComment(boardCommentNo)
                ? ResponseEntity.ok("댓글 삭제 성공")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                              .body("댓글 삭제 실패");
    }
    
    // 댓글 수정
    @PutMapping("{boardCommentNo}")
    public ResponseEntity<String> updateComment(
            @PathVariable("boardCommentNo") int boardCommentNo, 
            @RequestBody GroupComment comment) {
        try {
            comment.setBoardCommentNo(boardCommentNo);
            groupCommentService.updateComment(comment);
            return ResponseEntity.ok("댓글 수정 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("댓글 수정 실패");
        }
    }
    
    // 댓글 좋아요
    @PostMapping("{boardCommentNo}/like")
    public ResponseEntity<String> likeComment(
            @PathVariable int boardCommentNo, 
            @RequestBody int userNo) {
        return groupCommentService.addLike(boardCommentNo, userNo)
                ? ResponseEntity.ok("좋아요 성공")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST)
                              .body("이미 좋아요를 눌렀습니다.");
    }
    
 // 대댓글 작성
    @PostMapping("/{commentNo}/reply")
    public ResponseEntity<String> addReply(
            @PathVariable("commentNo") int parentCommentNo,
            @RequestBody GroupComment reply) {
        try {
            reply.setParentCommentNo(parentCommentNo);
            reply.setCommentDepth(1);  // 대댓글 깊이 설정
            groupCommentService.addComment(reply);
            return ResponseEntity.status(HttpStatus.CREATED)
                               .body("대댓글이 작성되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("대댓글 작성에 실패했습니다.");
        }
    }

    // 대댓글 목록 조회 (특정 댓글의)
    @GetMapping("/{commentNo}/replies")
    public ResponseEntity<List<GroupComment>> getReplies(
            @PathVariable("commentNo") int parentCommentNo) {
        List<GroupComment> replies = groupCommentService.getRepliesByCommentNo(parentCommentNo);
        return ResponseEntity.ok(replies != null ? replies : new ArrayList<>());
    }
}
	

