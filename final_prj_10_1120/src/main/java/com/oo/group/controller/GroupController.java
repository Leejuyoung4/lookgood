package com.oo.group.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestBody;

import com.oo.group.model.dto.Group;
import com.oo.group.model.service.GroupService;


@RestController
@RequestMapping("/api/group")
@CrossOrigin(origins = "*")
public class GroupController {
    // 서비스 의존성 주입
    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }
    
    // 게시글 전체 조회
    @GetMapping
    public ResponseEntity<?> getAllGroups() {
        try {
            List<Group> getAllGroups = groupService.getGroupList();
            return new ResponseEntity<>(getAllGroups, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("모임 목록을 가져올 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 게시글 상세 조회
    @GetMapping("{gBoardNo}")
    public ResponseEntity<Group> detail(@PathVariable("gBoardNo") int gBoardNo) {
        Group group = groupService.readGroup(gBoardNo);
        
        if (group != null) {
            return ResponseEntity.ok(group);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    
    // 게시글 등록
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createGroup(
        @RequestPart("gBoardTitle") String gBoardTitle,
        @RequestPart("gBoardContent") String gBoardContent,
        @RequestPart(value = "gBoardFile", required = false) MultipartFile gBoardFile) {

        try {
            Group group = new Group();
            group.setgBoardTitle(gBoardTitle);
            group.setgBoardContent(gBoardContent);
            group.setgBoardFile(gBoardFile != null ? gBoardFile.getOriginalFilename() : null);

            // 파일 저장 로직 추가 필요
            groupService.createGroup(group);

            return new ResponseEntity<>(group, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("게시글 등록 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    // 게시글 삭제
    @DeleteMapping("{gBoardNo}")
    public ResponseEntity<?> deleteGroup(@PathVariable("gBoardNo") int gBoardNo) {
        try {
            groupService.deleteGroup(gBoardNo);
            return ResponseEntity.ok("삭제 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    
    // 게시글 수정
    @PutMapping("{gBoardNo}")
    public ResponseEntity<?> updateGroup(@PathVariable("gBoardNo") int gBoardNo, @RequestBody Group group) {
        try {
            group.setgBoardNo(gBoardNo);
            groupService.updateGroup(group);
            return ResponseEntity.ok("수정 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 게시글 검색
    @GetMapping("/search")
    public ResponseEntity<List<Group>> searchPosts(
        @RequestParam(value = "keyword", required = false) String keyword) {
        try {
        	System.out.println(groupService.searchPosts(keyword));
            List<Group> posts = groupService.searchPosts(keyword);
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 조회수 증가
    @PutMapping("/{gBoardNo}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable int gBoardNo) {
        try {
            groupService.incrementViewCount(gBoardNo);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 좋아요수 증가
    @PutMapping("/{gBoardNo}/like")
    public ResponseEntity<Boolean> toggleLike(@PathVariable int gBoardNo, @RequestParam int userNo) {
        try {
            boolean isLiked = groupService.toggleLike(gBoardNo, userNo);
            return new ResponseEntity<>(isLiked, HttpStatus.OK); // 좋아요 상태 반환
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{gBoardNo}/like")
    public ResponseEntity<Boolean> isUserLiked(@PathVariable int gBoardNo, @RequestParam int userNo) {
        try {
            boolean isLiked = groupService.isUserLiked(gBoardNo, userNo);
            return new ResponseEntity<>(isLiked, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    
}