package com.oo.inquiry.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.oo.config.JwtUtil;
import com.oo.inquiry.model.dto.Inquiry;
import com.oo.inquiry.model.service.InquiryService;
import com.oo.user.model.dto.User;
import com.oo.user.model.service.UserService;


@RestController
@RequestMapping("/api/inquiry")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class InquiryController {
	// 서비스 의존성 주입
	private final InquiryService inquiryService;
	private final JwtUtil jwtUtil;
	private UserService userService;
	
	@Autowired
	public InquiryController(InquiryService inquiryService, JwtUtil jwtUtil, UserService userService) {
		super();
		this.inquiryService = inquiryService;
		this.jwtUtil = jwtUtil;
		this.userService = userService;
	}

	// 게시글 전체 조회
	@GetMapping
	public ResponseEntity<?> getAllInquiries() {
		try {
			List<Inquiry> list = inquiryService.getInquiryList();
			return new ResponseEntity<>(list, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("문의사항 목록을 가져올 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 게시글 상세 조회
	@GetMapping("{iNo}")
	public ResponseEntity<?> detailInquiry(@PathVariable("iNo") int iNo) {
		Inquiry inquiry = inquiryService.readInquiry(iNo);
		
		if (inquiry != null) {
			return ResponseEntity.ok(inquiry);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
	
	// 게시글 등록
	@PostMapping
	public ResponseEntity<?> createInquiry(
	    @RequestPart("inquiry") Inquiry inquiry,
	    @RequestPart(value = "files", required = false) List<MultipartFile> files) {
	    try {
	        // 사용자 번호 기본값 설정
	        if (inquiry.getUserNo() == null) {
	            inquiry.setUserNo(0); // 비로그인 사용자는 기본 사용자 번호로 처리
	        }

	        // 파일 저장 경로 설정
	        String uploadDir = "C:/SSAFY_prj/uploads/"; // 절대 경로로 설정
	        File directory = new File(uploadDir);
	        // if (!directory.exists()) {
	        //     directory.mkdirs(); // 디렉토리 생성
	        // }

	        // 파일 저장
	        if (files != null && !files.isEmpty()) {
	            StringBuilder filePaths = new StringBuilder();
	            for (MultipartFile file : files) {
	                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
	                Path filePath = Paths.get(uploadDir + fileName);
	                Files.write(filePath, file.getBytes());
	                filePaths.append(filePath.toString()).append(";");
	            }
	            inquiry.setiFiles(filePaths.toString());
	        }

	        // 서비스 호출
	        inquiryService.createInquiry(inquiry);

	        // 등록된 데이터 반환
	        return new ResponseEntity<>(inquiry, HttpStatus.CREATED);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>("글 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	 @GetMapping("/me")
	    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
	        try {
	            // Bearer 토큰에서 실제 JWT 값 추출
	            String jwt = token.replace("Bearer ", "");
	            String userId = jwtUtil.extractUsername(jwt); // 토큰에서 사용자 ID 추출
	            User user = userService.findUserById(userId); // 사용자 정보를 데이터베이스에서 조회

	            if (user == null) {
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 사용자입니다.");
	            }
	            return ResponseEntity.ok(user);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보를 가져올 수 없습니다.");
	        }
	    }

	// 게시글 삭제
	@DeleteMapping("{iNo}")
	public ResponseEntity<?> deleteInquiry(@PathVariable("iNo") int iNo) {
		try {
			inquiryService.deleteInquiry(iNo);
			return ResponseEntity.ok("삭제 성공");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	// 게시글 수정
	@PutMapping("{iNo}")
	public ResponseEntity<?> updateInquiry(@PathVariable("iNo") int iNo, @RequestBody Inquiry inquiry) {
		try {
			inquiry.setiNo(iNo);
			inquiryService.updateInquiry(inquiry);
			return ResponseEntity.ok("수정 성공");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	
	
	
	
}
