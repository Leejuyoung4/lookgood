package com.oo.video.model.dto;

import java.time.LocalDateTime;

public class SavedVideo {
    private int savedNo;         // 저장 번호
    private int userNo;          // 사용자 번호
    private int vNo;            // 비디오 번호
    private int progressRate;    // 진도율 (0-100%)
    private boolean isCompleted; // 완료 여부
    private LocalDateTime savedDate; // 저장 날짜

    // 기본 생성자
    public SavedVideo() {
    }

    // Getter와 Setter
    public int getSavedNo() {
        return savedNo;
    }

    public void setSavedNo(int savedNo) {
        this.savedNo = savedNo;
    }

    public int getUserNo() {
        return userNo;
    }

    public void setUserNo(int userNo) {
        this.userNo = userNo;
    }

    public int getVNo() {
        return vNo;
    }

    public void setVNo(int vNo) {
        this.vNo = vNo;
    }

    public int getProgressRate() {
        return progressRate;
    }

    public void setProgressRate(int progressRate) {
        this.progressRate = progressRate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public LocalDateTime getSavedDate() {
        return savedDate;
    }

    public void setSavedDate(LocalDateTime savedDate) {
        this.savedDate = savedDate;
    }

    @Override
    public String toString() {
        return "SavedVideo{" +
                "savedNo=" + savedNo +
                ", userNo=" + userNo +
                ", vNo=" + vNo +
                ", progressRate=" + progressRate +
                ", isCompleted=" + isCompleted +
                ", savedDate=" + savedDate +
                '}';
    }
}