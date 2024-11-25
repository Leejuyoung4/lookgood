package com.oo.video.model.dto;

import java.time.LocalDateTime;

public class VideoMemo {
    private int memoId;
    private int userNo;
    private int vNo;
    private String content;
    private int timeStamp;  // 카멜케이스로 변경
    private LocalDateTime createdAt;
    
    
    
    public VideoMemo() {}
    
    
	public VideoMemo(int memoId, int userNo, int vNo, String content, int timeStamp, LocalDateTime createdAt) {
		super();
		this.memoId = memoId;
		this.userNo = userNo;
		this.vNo = vNo;
		this.content = content;
		this.timeStamp = timeStamp;
		this.createdAt = createdAt;
	}

	
	
	public int getMemoId() {
		return memoId;
	}


	public void setMemoId(int memoId) {
		this.memoId = memoId;
	}


	public int getUserNo() {
		return userNo;
	}


	public void setUserNo(int userNo) {
		this.userNo = userNo;
	}


	public int getvNo() {
		return vNo;
	}


	public void setvNo(int vNo) {
		this.vNo = vNo;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public LocalDateTime getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

    public int getTimeStamp() { return timeStamp; }  // 카멜케이스로 변경
    public void setTimeStamp(int timeStamp) { this.timeStamp = timeStamp; }  // 카멜케이스로 변경


	@Override
	public String toString() {
		return "VideoMemo [memoId=" + memoId + ", userNo=" + userNo + ", vNo=" + vNo + ", content=" + content
				+ ", timeStamp=" + timeStamp + ", createdAt=" + createdAt + "]";
	}
    
    
}