package com.oo.video.model.dto;

public class Video {
    private int vNo;                 // 영상 번호
    private String vTitle;           // 영상 제목
    private String vDescription;     // 영상 설명
    private String vUploadDate;      // 업로드 날짜 (DATETIME은 String으로 처리)
    private int vViews;              // 조회수 (기본값 0)
    private int vLikes;              // 좋아요 수 (기본값 0)
    private int vHates;              // 싫어요 수 (기본값 0)
    private String vInstructor;      // 강사 이름
    private String vInstructorIntro; // 강사 소개
    private String vCategoryName;    // 카테고리 이름
    private int savedNo;             // 저장 번호
    private String videoId;          // YouTube 비디오 ID

    // 기본 생성자
    public Video() {}

    // 모든 필드를 포함한 생성자
    public Video(int vNo, String vTitle, String vDescription, String vUploadDate, 
                int vViews, int vLikes, int vHates, String vInstructor, 
                String vInstructorIntro, String vCategoryName, int savedNo, 
                String videoId) {
        this.vNo = vNo;
        this.vTitle = vTitle;
        this.vDescription = vDescription;
        this.vUploadDate = vUploadDate;
        this.vViews = vViews;
        this.vLikes = vLikes;
        this.vHates = vHates;
        this.vInstructor = vInstructor;
        this.vInstructorIntro = vInstructorIntro;
        this.vCategoryName = vCategoryName;
        this.savedNo = savedNo;
        this.videoId = videoId;
    }

    // Getter와 Setter
    public int getVNo() {
        return vNo;
    }

    public void setVNo(int vNo) {
        this.vNo = vNo;
    }

    public String getVTitle() {
        return vTitle;
    }

    public void setVTitle(String vTitle) {
        this.vTitle = vTitle;
    }

    public String getVDescription() {
        return vDescription;
    }

    public void setVDescription(String vDescription) {
        this.vDescription = vDescription;
    }

    public String getVUploadDate() {
        return vUploadDate;
    }

    public void setVUploadDate(String vUploadDate) {
        this.vUploadDate = vUploadDate;
    }

    public int getVViews() {
        return vViews;
    }

    public void setVViews(int vViews) {
        this.vViews = vViews;
    }

    public int getVLikes() {
        return vLikes;
    }

    public void setVLikes(int vLikes) {
        this.vLikes = vLikes;
    }

    public int getVHates() {
        return vHates;
    }

    public void setVHates(int vHates) {
        this.vHates = vHates;
    }

    public String getVInstructor() {
        return vInstructor;
    }

    public void setVInstructor(String vInstructor) {
        this.vInstructor = vInstructor;
    }

    public String getVInstructorIntro() {
        return vInstructorIntro;
    }

    public void setVInstructorIntro(String vInstructorIntro) {
        this.vInstructorIntro = vInstructorIntro;
    }

    public String getVCategoryName() {
        return vCategoryName;
    }

    public void setVCategoryName(String vCategoryName) {
        this.vCategoryName = vCategoryName;
    }

    public int getSavedNo() {
        return savedNo;
    }

    public void setSavedNo(int savedNo) {
        this.savedNo = savedNo;
    }
    
 // videoId의 Getter와 Setter 추가
    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    // toString 메서드 수정
    @Override
    public String toString() {
        return "Video{" +
                "vNo=" + vNo +
                ", vTitle='" + vTitle + '\'' +
                ", vDescription='" + vDescription + '\'' +
                ", vUploadDate='" + vUploadDate + '\'' +
                ", vViews=" + vViews +
                ", vLikes=" + vLikes +
                ", vHates=" + vHates +
                ", vInstructor='" + vInstructor + '\'' +
                ", vInstructorIntro='" + vInstructorIntro + '\'' +
                ", vCategoryName='" + vCategoryName + '\'' +
                ", savedNo=" + savedNo +
                ", videoId='" + videoId + '\'' +
                '}';
    }
}

