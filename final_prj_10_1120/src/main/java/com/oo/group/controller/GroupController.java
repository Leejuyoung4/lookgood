package com.oo.group.controller;

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
import org.springframework.web.bind.annotation.RestController;
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
    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
        try {
            groupService.createGroup(group);
            return new ResponseEntity<>(group, HttpStatus.CREATED); // 생성된 글 반환
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
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
    
    
    
    
    
    
}