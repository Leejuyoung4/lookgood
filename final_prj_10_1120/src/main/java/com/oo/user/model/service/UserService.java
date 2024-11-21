package com.oo.user.model.service;

import java.util.List;

import com.oo.user.model.dto.User;

public interface UserService {
	
	List<User> getAllUserList();
	User getUserDetail(int no);
	boolean signup(User user);
	User login(String userId, String password);
	boolean modify(User user);
	boolean remove(int no);
	boolean isUserIdDuplicate(String userId);
}
