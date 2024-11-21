package com.oo.eventdetail.model.dto;

public class EventDetail {
    private int eDetailsNo; // 상세 ID (PK)
    private String description; // 상세 설명
    private String address; // 주소
    private String inquiryNumber; // 문의 번호
    private String websiteUrl; // 웹사이트 URL
    private String checkInTime; // 체크인 시간
    private String checkOutTime; // 체크아웃 시간
    private boolean parkingAvailable; // 주차 가능 여부
    private String facilities; // 시설
    private int entryFee; // 참가비
    private int eNo; // 이벤트 ID (FK)

    // Getters and Setters
    public int geteDetailsNo() {
        return eDetailsNo;
    }

    public void seteDetailsNo(int eDetailsNo) {
        this.eDetailsNo = eDetailsNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInquiryNumber() {
        return inquiryNumber;
    }

    public void setInquiryNumber(String inquiryNumber) {
        this.inquiryNumber = inquiryNumber;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public boolean isParkingAvailable() {
        return parkingAvailable;
    }

    public void setParkingAvailable(boolean parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public int getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(int entryFee) {
        this.entryFee = entryFee;
    }

    public int geteNo() {
        return eNo;
    }

    public void seteNo(int eNo) {
        this.eNo = eNo;
    }

    @Override
    public String toString() {
        return "EventDetailDto{" +
                "eDetailsNo=" + eDetailsNo +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", inquiryNumber='" + inquiryNumber + '\'' +
                ", websiteUrl='" + websiteUrl + '\'' +
                ", checkInTime='" + checkInTime + '\'' +
                ", checkOutTime='" + checkOutTime + '\'' +
                ", parkingAvailable=" + parkingAvailable +
                ", facilities='" + facilities + '\'' +
                ", entryFee=" + entryFee +
                ", eNo=" + eNo +
                '}';
    }
}
