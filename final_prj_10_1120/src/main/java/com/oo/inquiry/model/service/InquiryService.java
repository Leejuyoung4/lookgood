package com.oo.inquiry.model.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.oo.inquiry.model.dto.Inquiry;

public interface InquiryService {
	// 게시글 전체 조회
	public List<Inquiry> getInquiryList();

	// 게시글 상세 조회
	public Inquiry readInquiry(int iNo);

	// 게시글 등록
	public void createInquiry(Inquiry inquiry);

	// 게시글 삭제
	public void deleteInquiry(int iNo);

	// 게시글 수정
	public void updateInquiry(int iNo, Inquiry inquiry, List<MultipartFile> files);

	// 조회수 증가
	public void incrementViews(int iNo);


	
	
}
