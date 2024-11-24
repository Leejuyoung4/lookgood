package com.oo.inquiry.model.service;

import java.util.List;

import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

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
	public void updateInquiry(Inquiry inquiry) {
		inquiryDao.modifyInquiry(inquiry);
	}

	// 조회수 증가
	@Override
	public void incrementViews(int iNo) {
		inquiryDao.increaseViews(iNo);
	}

	
	
	
	
}
