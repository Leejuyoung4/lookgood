package com.oo.inquirycomment.model.dto;

import java.time.LocalDateTime;

public class InquiryComment {
	private int iCommentNo;           // 댓글 번호
    private String iCommentContent;   // 댓글 내용
    private LocalDateTime iCommentRegDate;  // 댓글 등록일
    private LocalDateTime iCommentModDate;  // 댓글 수정일
    private int iNo;                  // 문의 게시글 번호
    private int userNo;               // 작성자 번호
    
	public InquiryComment() {}

	public InquiryComment(int iCommentNo, String iCommentContent, LocalDateTime iCommentRegDate,
			LocalDateTime iCommentModDate, int iNo, int userNo) {
		this.iCommentNo = iCommentNo;
		this.iCommentContent = iCommentContent;
		this.iCommentRegDate = iCommentRegDate;
		this.iCommentModDate = iCommentModDate;
		this.iNo = iNo;
		this.userNo = userNo;
	}

	public int getiCommentNo() {
		return iCommentNo;
	}

	public void setiCommentNo(int iCommentNo) {
		this.iCommentNo = iCommentNo;
	}

	public String getiCommentContent() {
		return iCommentContent;
	}

	public void setiCommentContent(String iCommentContent) {
		this.iCommentContent = iCommentContent;
	}

	public LocalDateTime getiCommentRegDate() {
		return iCommentRegDate;
	}

	public void setiCommentRegDate(LocalDateTime iCommentRegDate) {
		this.iCommentRegDate = iCommentRegDate;
	}

	public LocalDateTime getiCommentModDate() {
		return iCommentModDate;
	}

	public void setiCommentModDate(LocalDateTime iCommentModDate) {
		this.iCommentModDate = iCommentModDate;
	}

	public int getiNo() {
		return iNo;
	}

	public void setiNo(int iNo) {
		this.iNo = iNo;
	}

	public int getUserNo() {
		return userNo;
	}

	public void setUserNo(int userNo) {
		this.userNo = userNo;
	}

	@Override
	public String toString() {
		return "InquiryComment [iCommentNo=" + iCommentNo + ", iCommentContent=" + iCommentContent
				+ ", iCommentRegDate=" + iCommentRegDate + ", iCommentModDate=" + iCommentModDate + ", iNo=" + iNo
				+ ", userNo=" + userNo + "]";
	}
	
	

    
}
