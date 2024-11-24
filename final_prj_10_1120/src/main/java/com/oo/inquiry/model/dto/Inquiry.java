package com.oo.inquiry.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public class Inquiry {
    private int iNo; // 게시글 번호
    private String iTitle; // 제목
    private String iContents; // 내용
    private String iFiles; // 파일 경로
    private List<String> iFilesList;
    private LocalDateTime iRegDate; // 등록일
    private int iViews; // 조회수
    private boolean iIsResolved; // 해결 여부
    private int iCommentsCount; // 댓글 수
    private String iCategoryName; // 카테고리 이름
    private Integer userNo;

    // 기본 생성자
    public Inquiry() {}

	public Inquiry(int iNo, String iTitle, String iContents, String iFiles, List<String> iFilesList,
			LocalDateTime iRegDate, int iViews, boolean iIsResolved, int iCommentsCount, String iCategoryName,
			int userNo) {
		super();
		this.iNo = iNo;
		this.iTitle = iTitle;
		this.iContents = iContents;
		this.iFiles = iFiles;
		this.iFilesList = iFilesList;
		this.iRegDate = iRegDate;
		this.iViews = iViews;
		this.iIsResolved = iIsResolved;
		this.iCommentsCount = iCommentsCount;
		this.iCategoryName = iCategoryName;
		this.userNo = userNo;
	}

	public int getiNo() {
		return iNo;
	}

	public void setiNo(int iNo) {
		this.iNo = iNo;
	}

	public String getiTitle() {
		return iTitle;
	}

	public void setiTitle(String iTitle) {
		this.iTitle = iTitle;
	}

	public String getiContents() {
		return iContents;
	}

	public void setiContents(String iContents) {
		this.iContents = iContents;
	}

	public String getiFiles() {
		return iFiles;
	}

	public void setiFiles(String iFiles) {
		this.iFiles = iFiles;
	}

	public List<String> getiFilesList() {
		return iFilesList;
	}

	public void setiFilesList(List<String> iFilesList) {
		this.iFilesList = iFilesList;
	}

	public LocalDateTime getiRegDate() {
		return iRegDate;
	}

	public void setiRegDate(LocalDateTime iRegDate) {
		this.iRegDate = iRegDate;
	}

	public int getiViews() {
		return iViews;
	}

	public void setiViews(int iViews) {
		this.iViews = iViews;
	}

	public boolean isiIsResolved() {
		return iIsResolved;
	}

	public void setiIsResolved(boolean iIsResolved) {
		this.iIsResolved = iIsResolved;
	}

	public int getiCommentsCount() {
		return iCommentsCount;
	}

	public void setiCommentsCount(int iCommentsCount) {
		this.iCommentsCount = iCommentsCount;
	}

	public String getiCategoryName() {
		return iCategoryName;
	}

	public void setiCategoryName(String iCategoryName) {
		this.iCategoryName = iCategoryName;
	}

	public Integer getUserNo() {
		return userNo;
	}

	public void setUserNo(Integer userNo) {
		this.userNo = userNo;
	}

	@Override
	public String toString() {
		return "Inquiry [iNo=" + iNo + ", iTitle=" + iTitle + ", iContents=" + iContents + ", iFiles=" + iFiles
				+ ", iFilesList=" + iFilesList + ", iRegDate=" + iRegDate + ", iViews=" + iViews + ", iIsResolved="
				+ iIsResolved + ", iCommentsCount=" + iCommentsCount + ", iCategoryName=" + iCategoryName + ", userNo="
				+ userNo + "]";
	}

	
}