package com.oo.inquirycomment.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.oo.inquirycomment.model.dto.InquiryComment;

@Mapper
public interface InquiryCommentDao {

	// 댓글 목록
	List<InquiryComment> selectCommentsByInquiryNo(int iNo);

	// 댓글 등록
	int insertComment(InquiryComment inquiryComment);

	// 댓글 삭제
	int deleteComment(int iCommentNo);

	// 댓글 수정
	int updateComment(InquiryComment inquiryComment);

	
}
