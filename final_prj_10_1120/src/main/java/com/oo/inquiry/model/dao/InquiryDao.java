package com.oo.inquiry.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.multipart.MultipartFile;

import com.oo.inquiry.model.dto.Inquiry;

@Mapper
public interface InquiryDao {

	// 전체 게시글 조회
	public List<Inquiry> selectAll();

	// 상세 게시글 조회
	public Inquiry selectOne(int iNo);

	// 게시글 등록
	public void insertInquiry(Inquiry inquiry);

	// 게시글 삭제
	public void removeInquiry(int iNo);

	// 게시글 수정
	public void modifyInquiry(Inquiry inquiry);

	// 조회수 증가
	public void increaseViews(int iNo);




}