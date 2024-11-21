package com.oo.user.model.dto;

import java.time.LocalDateTime;

public class User {

	private int userNo;
	private String userId;
	private String password;
	private String userName;
	private String phoneNum;
	private String email;
	private LocalDateTime joinDate;

// 기본 생성자
	public User() {
	}

// 매개변수 있는 생성자
	public User(int userNo, String userId, String password, String userName, String phoneNum, String email,
			LocalDateTime joinDate) {
		this.userNo = userNo;
		this.userId = userId;
		this.password = password;
		this.userName = userName;
		this.phoneNum = phoneNum;
		this.email = email;
		this.joinDate = joinDate;
	}

// Getter 및 Setter 메서드
	public int getUserNo() {
		return userNo;
	}

	public void setUserNo(int userNo) {
		this.userNo = userNo;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDateTime getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(LocalDateTime joinDate) {
		this.joinDate = joinDate;
	}

// toString 메서드 (디버깅용)
	@Override
	public String toString() {
		return "UserDTO{" + "userNo=" + userNo + ", userId='" + userId + '\'' + ", password='" + password + '\''
				+ ", userName='" + userName + '\'' + ", phoneNum='" + phoneNum + '\'' + ", email='" + email + '\''
				+ ", joinDate=" + joinDate + '}';
	}
}