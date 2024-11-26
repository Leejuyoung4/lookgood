package com.oo.group.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays; // 추가
import java.util.Collections; // 추가
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
    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }
    
    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups() {
        try {
            List<Group> groups = groupService.getAllGroups();
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    
    // 게시글 상세 조회
    @GetMapping("{boardNo}")  // gBoardNo -> boardNo
    public ResponseEntity<Group> detail(@PathVariable("boardNo") int boardNo) {
        Group group = groupService.readGroup(boardNo);
        if (group != null) {
            if (group.getBoardFile() != null && !group.getBoardFile().isEmpty()) {
                group.setBoardFilesList(Arrays.asList(group.getBoardFile().split(",")));
                System.out.println(group.getBoardFilesList());
            } else {
                group.setBoardFilesList(Collections.emptyList());
            }
            return ResponseEntity.ok(group);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    
    // 게시글 등록
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createGroup(
        @RequestPart("boardTitle") String boardTitle,      // g 제거
        @RequestPart("boardContent") String boardContent,  // g 제거
        @RequestPart(value = "boardFiles", required = false) List<MultipartFile> boardFiles) {  // g 제거

        try {
            Group group = new Group();
            group.setBoardTitle(boardTitle);
            group.setBoardContent(boardContent);

            List<String> fileNames = new ArrayList<>();
            if (boardFiles != null) {
                for (MultipartFile file : boardFiles) {
                    String savedFileName = saveFile(file);
                    fileNames.add(savedFileName);
                }
            }
            group.setBoardFiles(String.join(",", fileNames));

            groupService.createGroup(group);
            return new ResponseEntity<>(group, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("게시글 등록 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // 게시글 삭제
    @DeleteMapping("{boardNo}")  // gBoardNo -> boardNo
    public ResponseEntity<?> deleteGroup(@PathVariable("boardNo") int boardNo) {
        try {
            groupService.deleteGroup(boardNo);
            return ResponseEntity.ok("삭제 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 게시글 수정
    @PutMapping("{boardNo}")  // gBoardNo -> boardNo
    public ResponseEntity<?> updateGroup(@PathVariable("boardNo") int boardNo, @RequestBody Group group) {
        try {
            group.setBoardNo(boardNo);
            System.out.println(group);
            groupService.updateGroup(group);
            return ResponseEntity.ok("수정 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private String saveFile(MultipartFile file) throws IOException {
    	String uploadDir = "C:/SSAFY_prj/uploads/"; // 절대 경로로 설정
    	String originalFilename = file.getOriginalFilename();
    	String savedFileName = System.currentTimeMillis() + "_" + originalFilename;
    	
    	File saveFile = new File(uploadDir + savedFileName);
    	if (!saveFile.getParentFile().exists()) {
    		saveFile.getParentFile().mkdirs(); // 디렉토리가 없으면 생성
    	}
    	file.transferTo(saveFile);
    	
    	return savedFileName; // 저장된 파일명 반환
    }

    // 조회수 증가
    @PutMapping("/{boardNo}/view")  // gBoardNo -> boardNo
    public ResponseEntity<Void> incrementViewCount(@PathVariable int boardNo) {
        try {
            groupService.incrementViewCount(boardNo);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 좋아요 관련 메서드들도 같은 방식으로 수정
    @PutMapping("/{boardNo}/like")  // gBoardNo -> boardNo
    public ResponseEntity<Boolean> toggleLike(@PathVariable int boardNo, @RequestParam int userNo) {
        if (userNo <= 0) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            boolean isLiked = groupService.toggleLike(boardNo, userNo);
            return new ResponseEntity<>(isLiked, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{boardNo}/like")  // gBoardNo -> boardNo
    public ResponseEntity<Boolean> isUserLiked(@PathVariable int boardNo, @RequestParam int userNo) {
        try {
            boolean isLiked = groupService.isUserLiked(boardNo, userNo);
            return new ResponseEntity<>(isLiked, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}











