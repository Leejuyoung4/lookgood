package com.oo.video.model.dto;

public class Video {
    // 기존 필드들...
    private int vNo;                 
    private String vTitle;           
    private String vDescription;     
    private String vUploadDate;      
    private int vViews;              
    private int vLikes;              
    private int vHates;              
    private String vInstructor;      
    private String vInstructorIntro; 
    private String vCategoryName;    
    private int savedNo;             
    private String videoId;          

    // 플레이리스트 관련 필드 추가
    private String playlistName;     // 플레이리스트 이름
    private int playlistOrder;       // 플레이리스트 내 순서
	
    public Video() {}

	public Video(int vNo, String vTitle, String vDescription, String vUploadDate, int vViews, int vLikes, int vHates,
			String vInstructor, String vInstructorIntro, String vCategoryName, int savedNo, String videoId,
			String playlistName, int playlistOrder) {
		super();
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
		this.playlistName = playlistName;
		this.playlistOrder = playlistOrder;
	}

	public int getvNo() {
		return vNo;
	}

	public void setvNo(int vNo) {
		this.vNo = vNo;
	}

	public String getvTitle() {
		return vTitle;
	}

	public void setvTitle(String vTitle) {
		this.vTitle = vTitle;
	}

	public String getvDescription() {
		return vDescription;
	}

	public void setvDescription(String vDescription) {
		this.vDescription = vDescription;
	}

	public String getvUploadDate() {
		return vUploadDate;
	}

	public void setvUploadDate(String vUploadDate) {
		this.vUploadDate = vUploadDate;
	}

	public int getvViews() {
		return vViews;
	}

	public void setvViews(int vViews) {
		this.vViews = vViews;
	}

	public int getvLikes() {
		return vLikes;
	}

	public void setvLikes(int vLikes) {
		this.vLikes = vLikes;
	}

	public int getvHates() {
		return vHates;
	}

	public void setvHates(int vHates) {
		this.vHates = vHates;
	}

	public String getvInstructor() {
		return vInstructor;
	}

	public void setvInstructor(String vInstructor) {
		this.vInstructor = vInstructor;
	}

	public String getvInstructorIntro() {
		return vInstructorIntro;
	}

	public void setvInstructorIntro(String vInstructorIntro) {
		this.vInstructorIntro = vInstructorIntro;
	}

	public String getvCategoryName() {
		return vCategoryName;
	}

	public void setvCategoryName(String vCategoryName) {
		this.vCategoryName = vCategoryName;
	}

	public int getSavedNo() {
		return savedNo;
	}

	public void setSavedNo(int savedNo) {
		this.savedNo = savedNo;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getPlaylistName() {
		return playlistName;
	}

	public void setPlaylistName(String playlistName) {
		this.playlistName = playlistName;
	}

	public int getPlaylistOrder() {
		return playlistOrder;
	}

	public void setPlaylistOrder(int playlistOrder) {
		this.playlistOrder = playlistOrder;
	}

	@Override
	public String toString() {
		return "Video [vNo=" + vNo + ", vTitle=" + vTitle + ", vDescription=" + vDescription + ", vUploadDate="
				+ vUploadDate + ", vViews=" + vViews + ", vLikes=" + vLikes + ", vHates=" + vHates + ", vInstructor="
				+ vInstructor + ", vInstructorIntro=" + vInstructorIntro + ", vCategoryName=" + vCategoryName
				+ ", savedNo=" + savedNo + ", videoId=" + videoId + ", playlistName=" + playlistName
				+ ", playlistOrder=" + playlistOrder + "]";
	}
    
    
    
    
}