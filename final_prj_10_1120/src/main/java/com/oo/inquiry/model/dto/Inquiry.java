package com.oo.inquiry.model.dto;

import java.time.LocalDateTime;

public class Inquiry {
    private int iNo; // 게시글 번호
    private String iTitle; // 제목
    private String iContents; // 내용
    private String iFile; // 파일 경로
    private LocalDateTime iRegDate; // 등록일
    private int iViews; // 조회수
    private boolean iIsResolved; // 해결 여부
    private int iCommentsCount; // 댓글 수
    private String iCategoryName; // 카테고리 이름

    // 기본 생성자
    public Inquiry() {}

    // 전체 필드를 포함한 생성자
    public Inquiry(int iNo, String iTitle, String iContents, String iFile, LocalDateTime iRegDate, int iViews,
                   boolean iIsResolved, int iCommentsCount, String iCategoryName) {
        this.iNo = iNo;
        this.iTitle = iTitle;
        this.iContents = iContents;
        this.iFile = iFile;
        this.iRegDate = iRegDate;
        this.iViews = iViews;
        this.iIsResolved = iIsResolved;
        this.iCommentsCount = iCommentsCount;
        this.iCategoryName = iCategoryName;
    }

    // Getter and Setter methods
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

    public String getiFile() {
        return iFile;
    }

    public void setiFile(String iFile) {
        this.iFile = iFile;
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

    @Override
    public String toString() {
        return "Inquiry [iNo=" + iNo + ", iTitle=" + iTitle + ", iContents=" + iContents + ", iFile=" + iFile
                + ", iRegDate=" + iRegDate + ", iViews=" + iViews + ", iIsResolved=" + iIsResolved
                + ", iCommentsCount=" + iCommentsCount + ", iCategoryName=" + iCategoryName + "]";
    }
}
