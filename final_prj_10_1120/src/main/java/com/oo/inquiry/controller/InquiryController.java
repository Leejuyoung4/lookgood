package com.oo.inquiry.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.oo.inquiry.model.dto.Inquiry;
import com.oo.inquiry.model.service.InquiryService;


@RestController
@RequestMapping("/api/inquiry")
@CrossOrigin(origins = "*") // 모든 출처 허용
public class InquiryController {
	// 서비스 의존성 주입
	private final InquiryService inquiryService;

	@Autowired
	public InquiryController(InquiryService inquiryService) {
		this.inquiryService = inquiryService;
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
