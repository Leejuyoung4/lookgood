package com.oo.group.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public class Group {
    private int boardNo;              // g 제거
    private String boardTitle;        // g 제거
    private String boardContent;      // g 제거
    private String boardFile;        // g 제거
    private List<String> boardFilesList;  // g 제거
    private LocalDateTime boardRegDate;   // g 제거
    private int boardViews;          // g 제거
    private boolean boardIsResolved;  // g 제거
    private int boardCommentsCount;   // g 제거
    private int boardLikeCount;       // g 제거
    private int boardHateCount;       // g 제거
    private int userNo;              // 이미 올바름
    
	public Group() {}

	public Group(int boardNo, String boardTitle, String boardContent, String boardFile, List<String> boardFilesList,
			LocalDateTime boardRegDate, int boardViews, boolean boardIsResolved, int boardCommentsCount,
			int boardLikeCount, int boardHateCount, int userNo) {
		super();
		this.boardNo = boardNo;
		this.boardTitle = boardTitle;
		this.boardContent = boardContent;
		this.boardFile = boardFile;
		this.boardFilesList = boardFilesList;
		this.boardRegDate = boardRegDate;
		this.boardViews = boardViews;
		this.boardIsResolved = boardIsResolved;
		this.boardCommentsCount = boardCommentsCount;
		this.boardLikeCount = boardLikeCount;
		this.boardHateCount = boardHateCount;
		this.userNo = userNo;
	}

	public int getBoardNo() {
		return boardNo;
	}

	public void setBoardNo(int boardNo) {
		this.boardNo = boardNo;
	}

	public String getBoardTitle() {
		return boardTitle;
	}

	public void setBoardTitle(String boardTitle) {
		this.boardTitle = boardTitle;
	}

	public String getBoardContent() {
		return boardContent;
	}

	public void setBoardContent(String boardContent) {
		this.boardContent = boardContent;
	}

	public String getBoardFile() {
		return boardFile;
	}

	public void setBoardFiles(String boardFile) {
		this.boardFile = boardFile;
	}

	public List<String> getBoardFilesList() {
		return boardFilesList;
	}

	public void setBoardFilesList(List<String> boardFilesList) {
		this.boardFilesList = boardFilesList;
	}

	public LocalDateTime getBoardRegDate() {
		return boardRegDate;
	}

	public void setBoardRegDate(LocalDateTime boardRegDate) {
		this.boardRegDate = boardRegDate;
	}

	public int getBoardViews() {
		return boardViews;
	}

	public void setBoardViews(int boardViews) {
		this.boardViews = boardViews;
	}

	public boolean isBoardIsResolved() {
		return boardIsResolved;
	}

	public void setBoardIsResolved(boolean boardIsResolved) {
		this.boardIsResolved = boardIsResolved;
	}

	public int getBoardCommentsCount() {
		return boardCommentsCount;
	}

	public void setBoardCommentsCount(int boardCommentsCount) {
		this.boardCommentsCount = boardCommentsCount;
	}

	public int getBoardLikeCount() {
		return boardLikeCount;
	}

	public void setBoardLikeCount(int boardLikeCount) {
		this.boardLikeCount = boardLikeCount;
	}

	public int getBoardHateCount() {
		return boardHateCount;
	}

	public void setBoardHateCount(int boardHateCount) {
		this.boardHateCount = boardHateCount;
	}

	public int getUserNo() {
		return userNo;
	}

	public void setUserNo(int userNo) {
		this.userNo = userNo;
	}

    
}