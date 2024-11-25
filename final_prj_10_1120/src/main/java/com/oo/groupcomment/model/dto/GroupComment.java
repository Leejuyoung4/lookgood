package com.oo.groupcomment.model.dto;

import java.time.LocalDateTime;

public class GroupComment {
    private int boardCommentNo;           // gBoard -> board
    private String boardCommentContent;    // gBoard -> board
    private LocalDateTime boardCommentRegDate;  // gBoard -> board
    private LocalDateTime boardCommentModDate;  // gBoard -> board
    private int boardNo;                  // gBoard -> board
    private int userNo;                   // 이미 올바름
	public GroupComment() {
		super();
		// TODO Auto-generated constructor stub
	}
	public GroupComment(int boardCommentNo, String boardCommentContent, LocalDateTime boardCommentRegDate,
			LocalDateTime boardCommentModDate, int boardNo, int userNo) {
		super();
		this.boardCommentNo = boardCommentNo;
		this.boardCommentContent = boardCommentContent;
		this.boardCommentRegDate = boardCommentRegDate;
		this.boardCommentModDate = boardCommentModDate;
		this.boardNo = boardNo;
		this.userNo = userNo;
	}
	public int getBoardCommentNo() {
		return boardCommentNo;
	}
	public void setBoardCommentNo(int boardCommentNo) {
		this.boardCommentNo = boardCommentNo;
	}
	public String getBoardCommentContent() {
		return boardCommentContent;
	}
	public void setBoardCommentContent(String boardCommentContent) {
		this.boardCommentContent = boardCommentContent;
	}
	public LocalDateTime getBoardCommentRegDate() {
		return boardCommentRegDate;
	}
	public void setBoardCommentRegDate(LocalDateTime boardCommentRegDate) {
		this.boardCommentRegDate = boardCommentRegDate;
	}
	public LocalDateTime getBoardCommentModDate() {
		return boardCommentModDate;
	}
	public void setBoardCommentModDate(LocalDateTime boardCommentModDate) {
		this.boardCommentModDate = boardCommentModDate;
	}
	public int getBoardNo() {
		return boardNo;
	}
	public void setBoardNo(int boardNo) {
		this.boardNo = boardNo;
	}
	public int getUserNo() {
		return userNo;
	}
	public void setUserNo(int userNo) {
		this.userNo = userNo;
	}
	@Override
	public String toString() {
		return "GroupComment [boardCommentNo=" + boardCommentNo + ", boardCommentContent=" + boardCommentContent
				+ ", boardCommentRegDate=" + boardCommentRegDate + ", boardCommentModDate=" + boardCommentModDate
				+ ", boardNo=" + boardNo + ", userNo=" + userNo + "]";
	}
    
    // 생성자, getter/setter도 모두 board로 변경 필요
    
    
}