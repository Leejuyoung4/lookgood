package com.oo.inquirycomment.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.oo.inquirycomment.model.dao.InquiryCommentDao;
import com.oo.inquirycomment.model.dto.InquiryComment;

@Service
public class InquiryCommentServiceImpl implements InquiryCommentService {
	private final InquiryCommentDao inquiryCommentDao;

	public InquiryCommentServiceImpl(InquiryCommentDao inquiryCommentDao) {
		this.inquiryCommentDao = inquiryCommentDao;
	}

	// 댓글 목록
	@Override
	public List<InquiryComment> getCommentsBygINo(int iNo) {
		return inquiryCommentDao.selectCommentsByInquiryNo(iNo);
	}

	// 댓글 등록
	@Override
	public boolean addComment(InquiryComment inquiryComment) {
		return inquiryCommentDao.insertComment(inquiryComment) > 0;
	}

	// 댓글 삭제
	@Override
	public boolean deleteComment(int iCommentNo) {
		return inquiryCommentDao.deleteComment(iCommentNo) > 0;
	}

	// 댓글 수정
	@Override
	public boolean updateComment(InquiryComment inquiryComment) {
		return inquiryCommentDao.updateComment(inquiryComment) > 0;
	}

}
