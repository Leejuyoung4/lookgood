package com.oo.inquiry.model.service;

import java.util.List;

import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.oo.inquiry.model.dao.InquiryDao;
import com.oo.inquiry.model.dto.Inquiry;

@Service
public class InquiryServiceImpl implements InquiryService {
	
	private final InquiryDao inquiryDao;
	private final ResourceLoader resourceLoader;
	
	public InquiryServiceImpl(InquiryDao inquiryDao, ResourceLoader resourceLoader) {
		this.inquiryDao = inquiryDao;
		this.resourceLoader = resourceLoader;
	}

	// 전체 게시글 조회
	@Override
	public List<Inquiry> getInquiryList() {
		return inquiryDao.selectAll();
	}

	// 상세 게시글 조회
	@Override
	public Inquiry readInquiry(int iNo) {
		return inquiryDao.selectOne(iNo);
	}

	// 게시글 등록
	@Override
	public void createInquiry(Inquiry inquiry) {
		inquiryDao.insertInquiry(inquiry);
	}

	// 게시글 삭제
	@Override
	public void deleteInquiry(int iNo) {
		inquiryDao.removeInquiry(iNo);
	}

	// 게시글 수정
	@Override
	public void updateInquiry(int iNo, Inquiry inquiry, List<MultipartFile> files) {
	    // 기존 iFiles 값을 병합하여 수정
	    if (files != null && !files.isEmpty()) {
	        StringBuilder filePaths = new StringBuilder();
	        for (MultipartFile file : files) {
	            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
	            // 파일 저장 경로 생성
	            filePaths.append("uploads/" + fileName).append(";");
	        }
	        inquiry.setiFiles(filePaths.toString());
	    }

	    inquiry.setiNo(iNo); // iNo 설정
	    inquiryDao.modifyInquiry(inquiry); // 수정 쿼리 호출
	}


	// 조회수 증가
	@Override
	public void incrementViews(int iNo) {
		inquiryDao.increaseViews(iNo);
	}

	
	
	
	
}
