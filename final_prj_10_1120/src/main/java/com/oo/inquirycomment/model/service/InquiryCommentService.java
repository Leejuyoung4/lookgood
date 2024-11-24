package com.oo.inquirycomment.model.service;

import java.util.List;

import com.oo.inquirycomment.model.dto.InquiryComment;

public interface InquiryCommentService {

	// 댓글 목록
	List<InquiryComment> getCommentsBygINo(int iNo);

	// 댓글 등록
	boolean addComment(InquiryComment inquiryComment);

	// 댓글 삭제
	boolean deleteComment(int iCommentNo);

	// 댓글 수정
	boolean updateComment(InquiryComment inquiryComment);

}
