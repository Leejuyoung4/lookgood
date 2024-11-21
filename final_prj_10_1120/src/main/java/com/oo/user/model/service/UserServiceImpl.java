package com.oo.user.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.oo.user.model.dao.UserDao;
import com.oo.user.model.dto.User;

@Service
public class UserServiceImpl implements UserService {
	
	private final UserDao userDao;

	public UserServiceImpl(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public List<User> getAllUserList() {
		return userDao.selectAllUser();
	}

	@Override
	public User getUserDetail(int no) {
		return userDao.selectUserByUserNo(no);
	}

	public boolean signup(User user) {
        try {
            int result = userDao.insertUser(user); // 삽입 성공 시 result가 1 이상이 됨
            return result > 0; // 삽입된 행의 개수가 1 이상이면 true 반환
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
	
	// 로그인
	@Override
	public User login(String id, String password) {
	    Map<String, String> info = new HashMap<>();
	    info.put("userId", id);
	    info.put("password", password);
	    return userDao.selectOne(info); // MyBatis 쿼리 실행
	}

	@Override
    public boolean modify(User user) {
        return userDao.updateUser(user) > 0; 
    }

	@Override
	public boolean remove(int userNo) {
		return userDao.deleteUser(userNo) > 0;
	}
	
	
	public boolean isUserIdDuplicate(String userId) {
        return userDao.countByUserId(userId) > 0;
    }

	

}
