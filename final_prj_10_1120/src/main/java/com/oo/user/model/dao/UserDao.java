package com.oo.user.model.dao;

import java.util.List;
import java.util.Map;

import com.oo.user.model.dto.User;

public interface UserDao {
    /**
     * 전체 사용자 목록을 조회합니다.
     * @return 전체 사용자 목록
     */
    List<User> selectAllUser();

    /**
     * 사용자 번호로 사용자 정보를 조회합니다.
     * @param no 조회할 사용자 번호
     * @return 사용자 정보
     */
    User selectUserByUserNo(int no);

    /**
     * 사용자 아이디로 사용자 정보를 조회합니다.
     * @param userId 조회할 사용자 아이디
     * @return 사용자 정보
     */
    User selectUserByUserId(String userId);

    /**
     * 로그인 정보로 사용자를 조회합니다.
     * @param map userId와 password를 담은 Map
     * @return 사용자 정보
     * @deprecated PasswordUtil.verify() 사용으로 인해 더 이상 사용되지 않음
     */
    @Deprecated
    User selectOne(Map<String, String> map);

    /**
     * 새로운 사용자를 등록합니다.
     * 비밀번호는 PasswordUtil.encrypt()로 암호화되어 저장됩니다.
     * @param user 등록할 사용자 정보
     * @return 등록된 행의 수
     */
    int insertUser(User user);

    /**
     * 사용자 정보를 수정합니다.
     * 비밀번호가 포함된 경우 PasswordUtil.encrypt()로 암호화됩니다.
     * @param user 수정할 사용자 정보
     * @return 수정된 행의 수
     */
    int updateUser(User user);

    /**
     * 사용자를 삭제합니다.
     * @param no 삭제할 사용자 번호
     * @return 삭제된 행의 수
     */
    int deleteUser(int no);

    /**
     * 아이디 중복을 확인합니다.
     * @param userId 확인할 아이디
     * @return 중복된 아이디의 수
     */
    int countByUserId(String userId);

    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보
     */
    User selectUserByUserId(String userId);
}