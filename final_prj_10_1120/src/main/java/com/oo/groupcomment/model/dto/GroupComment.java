package com.oo.groupcomment.model.dto;

import java.time.LocalDateTime;

public class GroupComment {
    private int gBoardCommentNo;        // 댓글 고유 번호
    private String gBoardCommentContent; // 댓글 내용
    private LocalDateTime gBoardCommentRegDate; // 댓글 등록일
    private LocalDateTime gBoardCommentModDate; // 댓글 수정일
    private int gBoardNo;              // 게시글 번호
    private int userNo;                // 사용자 번호
    
	public GroupComment() {}

	public GroupComment(int gBoardCommentNo, String gBoardCommentContent, LocalDateTime gBoardCommentRegDate,
			LocalDateTime gBoardCommentModDate, int gBoardNo, int userNo) {
		this.gBoardCommentNo = gBoardCommentNo;
		this.gBoardCommentContent = gBoardCommentContent;
		this.gBoardCommentRegDate = gBoardCommentRegDate;
		this.gBoardCommentModDate = gBoardCommentModDate;
		this.gBoardNo = gBoardNo;
		this.userNo = userNo;
	}

	public int getgBoardCommentNo() {
		return gBoardCommentNo;
	}

	public void setgBoardCommentNo(int gBoardCommentNo) {
		this.gBoardCommentNo = gBoardCommentNo;
	}

	public String getgBoardCommentContent() {
		return gBoardCommentContent;
	}

	public void setgBoardCommentContent(String gBoardCommentContent) {
		this.gBoardCommentContent = gBoardCommentContent;
	}

	public LocalDateTime getgBoardCommentRegDate() {
		return gBoardCommentRegDate;
	}

	public void setgBoardCommentRegDate(LocalDateTime gBoardCommentRegDate) {
		this.gBoardCommentRegDate = gBoardCommentRegDate;
	}

	public LocalDateTime getgBoardCommentModDate() {
		return gBoardCommentModDate;
	}

	public void setgBoardCommentModDate(LocalDateTime gBoardCommentModDate) {
		this.gBoardCommentModDate = gBoardCommentModDate;
	}

	public int getgBoardNo() {
		return gBoardNo;
	}

	public void setgBoardNo(int gBoardNo) {
		this.gBoardNo = gBoardNo;
	}

	public int getUserNo() {
		return userNo;
	}

	public void setUserNo(int userNo) {
		this.userNo = userNo;
	}

	@Override
	public String toString() {
		return "GroupComment [gBoardCommentNo=" + gBoardCommentNo + ", gBoardCommentContent=" + gBoardCommentContent
				+ ", gBoardCommentRegDate=" + gBoardCommentRegDate + ", gBoardCommentModDate=" + gBoardCommentModDate
				+ ", gBoardNo=" + gBoardNo + ", userNo=" + userNo + "]";
	}
}