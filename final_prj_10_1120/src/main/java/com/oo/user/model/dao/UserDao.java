package com.oo.user.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.oo.user.model.dto.User;

@Mapper
public interface UserDao {
	// 전체 사용자 목록 조회
	List<User> selectAllUser();
	
	// user_no로 특정 사용자 목록 조회
	User selectUserByUserNo(int no);
	
	// 사용자 등록
	int insertUser(User user);
	
	// 로그인에서 특정 사용자 조회
	User selectOne(Map<String, String> info);
	
	// 회원정보수정
	int updateUser(User user);
	
	// 회원탈퇴
	int deleteUser(int userNo);

	int countByUserId(String userId);
}
