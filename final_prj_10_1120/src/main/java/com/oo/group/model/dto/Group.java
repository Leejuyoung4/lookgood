package com.oo.group.model.dto;

import java.time.LocalDateTime;

public class Group {
	private int gBoardNo;
    private String gBoardTitle;
    private String gBoardContent;
    private String gBoardFile;
    private LocalDateTime gBoardRegDate;
    private int gBoardViews;
    private boolean gBoardIsResolved;
    private int gBoardCommentsCount;
    private int gBoardLikeCount;
    private int gBoardHateCount;
    private int userNo;
 
	public Group() {}

	public Group(int gBoardNo, String gBoardTitle, String gBoardContent, String gBoardFile, LocalDateTime gBoardRegDate,
			int gBoardViews, boolean gBoardIsResolved, int gBoardCommentsCount, int gBoardLikeCount,
			int gBoardHateCount, int userNo) {
		this.gBoardNo = gBoardNo;
		this.gBoardTitle = gBoardTitle;
		this.gBoardContent = gBoardContent;
		this.gBoardFile = gBoardFile;
		this.gBoardRegDate = gBoardRegDate;
		this.gBoardViews = gBoardViews;
		this.gBoardIsResolved = gBoardIsResolved;
		this.gBoardCommentsCount = gBoardCommentsCount;
		this.gBoardLikeCount = gBoardLikeCount;
		this.gBoardHateCount = gBoardHateCount;
		this.userNo = userNo;
	}

	public int getgBoardNo() {
		return gBoardNo;
	}

	public void setgBoardNo(int gBoardNo) {
		this.gBoardNo = gBoardNo;
	}

	public String getgBoardTitle() {
		return gBoardTitle;
	}

	public void setgBoardTitle(String gBoardTitle) {
		this.gBoardTitle = gBoardTitle;
	}

	public String getgBoardContent() {
		return gBoardContent;
	}

	public void setgBoardContent(String gBoardContent) {
		this.gBoardContent = gBoardContent;
	}

	public String getgBoardFile() {
		return gBoardFile;
	}

	public void setgBoardFile(String gBoardFile) {
		this.gBoardFile = gBoardFile;
	}

	public LocalDateTime getgBoardRegDate() {
		return gBoardRegDate;
	}

	public void setgBoardRegDate(LocalDateTime gBoardRegDate) {
		this.gBoardRegDate = gBoardRegDate;
	}

	public int getgBoardViews() {
		return gBoardViews;
	}

	public void setgBoardViews(int gBoardViews) {
		this.gBoardViews = gBoardViews;
	}

	public boolean isgBoardIsResolved() {
		return gBoardIsResolved;
	}

	public void setgBoardIsResolved(boolean gBoardIsResolved) {
		this.gBoardIsResolved = gBoardIsResolved;
	}

	public int getgBoardCommentsCount() {
		return gBoardCommentsCount;
	}

	public void setgBoardCommentsCount(int gBoardCommentsCount) {
		this.gBoardCommentsCount = gBoardCommentsCount;
	}

	public int getgBoardLikeCount() {
		return gBoardLikeCount;
	}

	public void setgBoardLikeCount(int gBoardLikeCount) {
		this.gBoardLikeCount = gBoardLikeCount;
	}

	public int getgBoardHateCount() {
		return gBoardHateCount;
	}

	public void setgBoardHateCount(int gBoardHateCount) {
		this.gBoardHateCount = gBoardHateCount;
	}

	public int getUserNo() {
		return userNo;
	}

	public void setUserNo(int userNo) {
		this.userNo = userNo;
	}

	@Override
	public String toString() {
		return "Group [gBoardNo=" + gBoardNo + ", gBoardTitle=" + gBoardTitle + ", gBoardContent=" + gBoardContent
				+ ", gBoardFile=" + gBoardFile + ", gBoardRegDate=" + gBoardRegDate + ", gBoardViews=" + gBoardViews
				+ ", gBoardIsResolved=" + gBoardIsResolved + ", gBoardCommentsCount=" + gBoardCommentsCount
				+ ", gBoardLikeCount=" + gBoardLikeCount + ", gBoardHateCount=" + gBoardHateCount + ", userNo=" + userNo
				+ "]";
	}
}