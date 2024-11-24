package com.oo.user.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oo.config.PasswordUtil;
import com.oo.user.model.dao.UserDao;
import com.oo.user.model.dto.User;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUserList() {
        return userDao.selectAllUser();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserDetail(int no) {
        return userDao.selectUserByUserNo(no);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUserId(String userId) {
        try {
            return userDao.selectUserByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean signup(User user) {
        try {
            // 필수 필드 검증
            if (user.getUserId() == null || user.getPassword() == null || 
                user.getUserName() == null || user.getEmail() == null) {
                return false;
            }

            // 아이디 중복 검사
            if (isUserIdDuplicate(user.getUserId())) {
                return false;
            }

            // 비밀번호가 암호화되지 않은 경우에만 암호화
            if (!user.getPassword().contains(":")) {
                user.setPassword(PasswordUtil.encrypt(user.getPassword()));
            }

            // 회원 등록
            return userDao.insertUser(user) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User login(String userId, String password) {
        try {
            // 사용자 조회
            User user = userDao.selectUserByUserId(userId);
            if (user == null) {
                return null;
            }

            // 비밀번호 검증
            if (PasswordUtil.verify(password, user.getPassword())) {
                return user;
            }
            
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean modify(User user) {
        try {
            // 비밀번호가 변경되는 경우에만 암호화
            if (user.getPassword() != null && !user.getPassword().trim().isEmpty() 
                && !user.getPassword().contains(":")) {
                user.setPassword(PasswordUtil.encrypt(user.getPassword()));
            }
            return userDao.updateUser(user) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean remove(int no) {
        try {
            return userDao.deleteUser(no) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserIdDuplicate(String userId) {
        try {
            return userDao.countByUserId(userId) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

	@Override
	public User findUserById(String userId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean isPhoneNumDuplicate(String phoneNum) {
	    try {
	        return userDao.countByPhoneNum(phoneNum) > 0;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
}