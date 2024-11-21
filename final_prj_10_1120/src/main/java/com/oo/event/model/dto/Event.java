package com.oo.event.model.dto;

import java.util.Date;

public class Event {
    private int eNo;                // 이벤트 고유 번호
    private String eTitle;          // 이벤트 제목
    private String eShort;          // 간단 설명
    private String eContent;        // 상세 설명
    private Date eStartDate;        // 시작일
    private Date eEndDate;          // 종료일
    private String eBigCity;        // 대도시 이름
    private String eSmallCity;      // 소도시 이름

    // 기본 생성자
    public Event() {}

    // 매개변수 있는 생성자
    public Event(int eNo, String eTitle, String eShort, String eContent, Date eStartDate, Date eEndDate, String eBigCity, String eSmallCity) {
        this.eNo = eNo;
        this.eTitle = eTitle;
        this.eShort = eShort;
        this.eContent = eContent;
        this.eStartDate = eStartDate;
        this.eEndDate = eEndDate;
        this.eBigCity = eBigCity;
        this.eSmallCity = eSmallCity;
    }

    // Getter & Setter
    public int geteNo() {
        return eNo;
    }

    public void seteNo(int eNo) {
        this.eNo = eNo;
    }

    public String geteTitle() {
        return eTitle;
    }

    public void seteTitle(String eTitle) {
        this.eTitle = eTitle;
    }

    public String geteShort() {
        return eShort;
    }

    public void seteShort(String eShort) {
        this.eShort = eShort;
    }

    public String geteContent() {
        return eContent;
    }

    public void seteContent(String eContent) {
        this.eContent = eContent;
    }

    public Date geteStartDate() {
        return eStartDate;
    }

    public void seteStartDate(Date eStartDate) {
        this.eStartDate = eStartDate;
    }

    public Date geteEndDate() {
        return eEndDate;
    }

    public void seteEndDate(Date eEndDate) {
        this.eEndDate = eEndDate;
    }

    public String geteBigCity() {
        return eBigCity;
    }

    public void seteBigCity(String eBigCity) {
        this.eBigCity = eBigCity;
    }

    public String geteSmallCity() {
        return eSmallCity;
    }

    public void seteSmallCity(String eSmallCity) {
        this.eSmallCity = eSmallCity;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eNo=" + eNo +
                ", eTitle='" + eTitle + '\'' +
                ", eShort='" + eShort + '\'' +
                ", eContent='" + eContent + '\'' +
                ", eStartDate=" + eStartDate +
                ", eEndDate=" + eEndDate +
                ", eBigCity='" + eBigCity + '\'' +
                ", eSmallCity='" + eSmallCity + '\'' +
                '}';
    }
}
